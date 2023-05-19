package com.project.DistanceTracker.services;


import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class Calculator {

    public double getDistanceFromFile(MultipartFile file) {
        String line;
        double previousLatitude = 0;
        double previousLongitude = 0;
        double totalDistance = 0;
        double distance = 0;


        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            while ((line = reader.readLine()) != null) {

                if (line.startsWith("$GPGGA")) {
                    String[] gpggaParts = line.split(",");
                    String latitude = gpggaParts[2];
                    String longitude = gpggaParts[4];

                    String latDirection = gpggaParts[3];
                    String lonDirection = gpggaParts[5];

                    if (latitude.length() >= 4 && longitude.length() >= 5) {

                        double latValue = parseCoordinate(latitude, latDirection);
                        double lonValue = parseCoordinate(longitude, lonDirection);

                        if (previousLatitude != 0 && previousLongitude != 0) {
                            distance = calculateDistance(previousLatitude, previousLongitude, latValue, lonValue);
                            totalDistance += distance;
                        }

                        previousLatitude = latValue;
                        previousLongitude = lonValue;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return totalDistance;
    }

    public double getDistanceFromLine(String line) {
        String[] data = line.split("\\$");

        int n = 0;

        double previousLatitude = 0.0;
        double previousLongitude = 0.0;
        double totalDistance = 0.0;

        double distance = 0;

        while (n < data.length) {

            if (data[n].startsWith("GPGGA")) {
                String[] gpggaParts = data[n].split(",");

                if (gpggaParts.length >= 6) {

                    String latitude = gpggaParts[2];
                    String longitude = gpggaParts[4];

                    String latDirection = gpggaParts[3];
                    String lonDirection = gpggaParts[5];


                    if (latitude.length() >= 4 && longitude.length() >= 5) {

                        double latValue = parseCoordinate(latitude, latDirection);
                        double lonValue = parseCoordinate(longitude, lonDirection);

                        if (previousLatitude != 0.0 && previousLongitude != 0.0) {
                            distance = calculateDistance(previousLatitude, previousLongitude, latValue, lonValue);
                            totalDistance += distance;
                        }

                        previousLatitude = latValue;
                        previousLongitude = lonValue;
                    }
                }
            }
            if (data[n].startsWith("$GNVTG") && data[n].length() >= 8) {
                String[] gnvtgParts = data[n].split(",");
                String speed = gnvtgParts[5];
                if (Double.parseDouble(speed) == 0) {
                    totalDistance = totalDistance - distance;
                }
            }
            n++;
        }
        return totalDistance;
    }


    private static double parseCoordinate(String coordinate, String direction) {
        double value = 0;

        double degrees = Double.parseDouble(coordinate.substring(0, 2));
        double minutes = Double.parseDouble(coordinate.substring(2));

        value = degrees + minutes / 60.0;


        if (direction.equals("S") || direction.equals("W")) {
            value *= -1;
        }


        return Math.toRadians(value);
    }

    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371210;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }


}
