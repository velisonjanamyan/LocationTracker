package com.example.locationtracking.service;

import com.example.locationtracking.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private LocationConsumer locationConsumer;

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }

    @Scheduled(fixedRate = 60000)
    public void generateReport() {
        String personId = "person-1";
        List<Location> locations = locationConsumer.getLocationsForPerson(personId);
        if (locations.size() < 2) {
            System.out.println("Not enough data for report for person " + personId);
            return;
        }
        double totalDistance = 0;
        for (int i = 1; i < locations.size(); i++) {
            Location prev = locations.get(i - 1);
            Location curr = locations.get(i);
            totalDistance += haversine(prev.getLatitude(), prev.getLongitude(),
                    curr.getLatitude(), curr.getLongitude());
        }
        System.out.println("Total distance traveled by " + personId + " is: " + totalDistance + " km");
    }
}
