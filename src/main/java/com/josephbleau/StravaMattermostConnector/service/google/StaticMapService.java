package com.josephbleau.StravaMattermostConnector.service.google;

import de.pentabyte.googlemaps.Color;
import de.pentabyte.googlemaps.StaticMap;
import de.pentabyte.googlemaps.StaticPath;
import javastrava.model.StravaActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class StaticMapService {
    private final String apiKey;

    @Autowired
    public StaticMapService(@Value("${google.api-key}") final String apiKey) {
        this.apiKey = apiKey;
    }

    public String generateStaticMap(StravaActivity activity) {
        StaticPath path = new StaticPath(activity.getMap().getSummaryPolyline());
        path.setColor(Color.red);

        StaticMap map = new StaticMap(600, 400, apiKey);
        map.setMaptype(StaticMap.Maptype.roadmap);
        map.setPaths(Arrays.asList(path));

        return map.toString();
    }

}
