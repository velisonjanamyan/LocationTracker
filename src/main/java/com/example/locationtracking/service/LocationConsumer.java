package com.example.locationtracking.service;

import com.example.locationtracking.model.Location;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationConsumer {
    private final Map<String, List<Location>> locationData = new ConcurrentHashMap<>();
    private final Map<String, Double> totalDistances = new ConcurrentHashMap<>();
    private final Map<String, Double> lastLat = new ConcurrentHashMap<>();
    private final Map<String, Double> lastLon = new ConcurrentHashMap<>();


    @KafkaListener(topics = "person-locations", groupId = "location_group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(Location location) {
        locationData.computeIfAbsent(location.getPersonId(), k -> new ArrayList<>()).add(location);
        if (locationData.get(location.getPersonId()).size() > 1) {
            double distance = distanceInKm(lastLat.get(location.getPersonId()), lastLon.get(location.getPersonId()),
                    location.getLatitude(), location.getLongitude());
            Double v1 = totalDistances.computeIfPresent(location.getPersonId(), (k, v) -> v + distance);
            if(v1 == null) {
                totalDistances.put(location.getPersonId(), distance);
            }
        }

        lastLat.put(location.getPersonId(), location.getLatitude());
        lastLon.put(location.getPersonId(), location.getLongitude());

        LocalDateTime dateTime = location.getTimestamp();
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int dayOfMonth = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();


        System.out.printf("Received update from user %s at %d/%d/%d/%d:%d:%d -> total distance = %.4f km%n",
                location.getPersonId(),
                year, month, dayOfMonth, hour, minute, second,
                totalDistances.getOrDefault(location.getPersonId(), 0.0));
    }

    private static final double EARTH_RADIUS = 6371.0;
    public static double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    public List<Location> getLocationsForPerson(String personId) {
        return locationData.getOrDefault(personId, Collections.emptyList());
    }
}
