package org.mohyla.itinerary.routes.domain.models;

import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {

    @Id @GeneratedValue
    private Long id;

    private String startPoint;
    private String endPoint;

    protected Route() {}

    public Route(String startPoint, String endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    // getters
    public Long getId() { return id; }
    public String getStartPoint() { return startPoint; }
    public String getEndPoint() { return endPoint; }
}

