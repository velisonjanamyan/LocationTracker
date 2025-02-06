package com.example.locationtracking.service;

import com.example.locationtracking.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LocationProducer {

    private static final String TOPIC = "person-locations";

    @Autowired
    private KafkaTemplate<String, Location> kafkaTemplate;

    private Map<String, Location> lastLocations = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private final String person = "person-";

    @Scheduled(fixedRate = 3000)
    public void sendLocationUpdate() {
        int id = random.nextInt(1, 6);
        String personId = person + id;
        Location lastLocation = lastLocations.get(personId);
        Location newLocation = generateNewLocation(lastLocation, personId);
        lastLocations.put(personId, newLocation);

        kafkaTemplate.send(TOPIC, personId, newLocation);
        System.out.println("Sent location update: " + newLocation);
    }

    private Location generateNewLocation(Location lastLocation, String personId) {
        double lat, lon;
        if (lastLocation == null) {
            lat = 40.7128;
            lon = -74.0060;
        } else {
            lat = lastLocation.getLatitude() + (random.nextDouble() - 0.5) * 0.01;
            lon = lastLocation.getLongitude() + (random.nextDouble() - 0.5) * 0.01;
        }
        Location location = new Location();
        location.setPersonId(personId);
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setTimestamp(LocalDateTime.now());
        return location;
    }
}
