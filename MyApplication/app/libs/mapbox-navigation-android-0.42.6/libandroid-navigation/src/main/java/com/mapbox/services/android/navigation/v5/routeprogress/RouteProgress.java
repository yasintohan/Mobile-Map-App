package com.mapbox.services.android.navigation.v5.routeprogress;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteLeg;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.navigator.BannerInstruction;
import com.mapbox.navigator.VoiceInstruction;

import java.util.List;

/**
 * This class contains all progress information at any given time during a navigation session. This
 * progress includes information for the current route, leg and step the user is traversing along.
 * With every new valid location update, a new route progress will be generated using the latest
 * information.
 * <p>
 * The latest route progress object can be obtained through either the {@link ProgressChangeListener}
 * or the {@link com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener} callbacks.
 * Note that the route progress object's immutable.
 * </p>
 *
 * @since 0.1.0
 */
@AutoValue
public abstract class RouteProgress {

  /**
   * Get the route the navigation session is currently using. When a reroute occurs and a new
   * directions route gets obtained, with the next location update this directions route should
   * reflect the new route. All direction route get passed in through
   * {@link com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation#startNavigation(DirectionsRoute)}.
   *
   * @return a {@link DirectionsRoute} currently being used for the navigation session
   * @since 0.1.0
   */
  public abstract DirectionsRoute directionsRoute();

  /**
   * Index representing the current leg the user is on. If the directions route currently in use
   * contains more then two waypoints, the route is likely to have multiple legs representing the
   * distance between the two points.
   *
   * @return an integer representing the current leg the user is on
   * @since 0.1.0
   */
  public abstract int legIndex();

  /**
   * Provides the current {@link RouteLeg} the user is on.
   *
   * @return a {@link RouteLeg} the user is currently on
   * @since 0.1.0
   */
  @NonNull
  public RouteLeg currentLeg() {
    return directionsRoute().legs().get(legIndex());
  }

  /**
   * Total distance traveled in meters along route.
   *
   * @return a double value representing the total distance the user has traveled along the route,
   * using unit meters
   * @since 0.1.0
   */
  public double distanceTraveled() {
    double distanceTraveled = directionsRoute().distance() - distanceRemaining();
    if (distanceTraveled < 0) {
      distanceTraveled = 0;
    }
    return distanceTraveled;
  }

  /**
   * Provides the duration remaining in seconds till the user reaches the end of the route.
   *
   * @return {@code long} value representing the duration remaining till end of route, in unit
   * seconds
   * @since 0.1.0
   */
  public double durationRemaining() {
    return (1 - fractionTraveled()) * directionsRoute().duration();
  }

  /**
   * Get the fraction traveled along the current route, this is a float value between 0 and 1 and
   * isn't guaranteed to reach 1 before the user reaches the end of the route.
   *
   * @return a float value between 0 and 1 representing the fraction the user has traveled along the
   * route
   * @since 0.1.0
   */
  public float fractionTraveled() {
    float fractionRemaining = 1;

    if (directionsRoute().distance() > 0) {
      fractionRemaining = (float) (distanceTraveled() / directionsRoute().distance());
    }
    return fractionRemaining;
  }

  /**
   * Provides the distance remaining in meters till the user reaches the end of the route.
   *
   * @return {@code long} value representing the distance remaining till end of route, in unit meters
   * @since 0.1.0
   */
  public abstract double distanceRemaining();

  /**
   * Number of waypoints remaining on the current route.
   *
   * @return integer value representing the number of way points remaining along the route
   * @since 0.5.0
   */
  public int remainingWaypoints() {
    return directionsRoute().legs().size() - legIndex();
  }

  /**
   * Gives a {@link RouteLegProgress} object with information about the particular leg the user is
   * currently on.
   *
   * @return a {@link RouteLegProgress} object
   * @since 0.1.0
   */
  public abstract RouteLegProgress currentLegProgress();

  /**
   * Provides a list of points that represent the current step
   * step geometry.
   *
   * @return list of points representing the current step
   * @since 0.12.0
   */
  public abstract List<Point> currentStepPoints();

  /**
   * Provides a list of points that represent the upcoming step
   * step geometry.
   *
   * @return list of points representing the upcoming step
   * @since 0.12.0
   */
  @Nullable
  public abstract List<Point> upcomingStepPoints();

  /**
   * Returns whether or not the location updates are
   * considered in a tunnel along the route.
   *
   * @return true if in a tunnel, false otherwise
   * @since 0.19.0
   */
  public abstract boolean inTunnel();

  /**
   * Current voice instruction.
   *
   * @return current voice instruction
   * @since 0.20.0
   */
  @Nullable
  public abstract VoiceInstruction voiceInstruction();

  /**
   * Current banner instruction.
   *
   * @return current banner instruction
   * @since 0.25.0
   */
  @Nullable
  public abstract BannerInstruction bannerInstruction();

  /**
   * Returns the current state of progress along the route.  Provides route and location tracking
   * information.
   *
   * @return the current state of progress along the route.
   */
  @Nullable
  public abstract RouteProgressState currentState();

  /**
   * Returns the current {@link DirectionsRoute} geometry with a buffer
   * that encompasses visible tile surface are while navigating.
   * <p>
   * This {@link Geometry} is ideal for offline downloads of map or routing tile
   * data.
   *
   * @return current route geometry with buffer
   */
  @Nullable
  public abstract Geometry routeGeometryWithBuffer();

  public abstract RouteProgress.Builder toBuilder();

  abstract LegStep currentStep();

  abstract int stepIndex();

  abstract double legDistanceRemaining();

  abstract double stepDistanceRemaining();

  abstract double legDurationRemaining();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder directionsRoute(DirectionsRoute directionsRoute);

    abstract DirectionsRoute directionsRoute();

    public abstract Builder legIndex(int legIndex);

    abstract int legIndex();

    public abstract Builder stepIndex(int stepIndex);

    abstract int stepIndex();

    public abstract Builder legDistanceRemaining(double legDistanceRemaining);

    abstract double legDistanceRemaining();

    public abstract Builder legDurationRemaining(double durationRemaining);

    abstract double legDurationRemaining();

    public abstract Builder stepDistanceRemaining(double stepDistanceRemaining);

    abstract double stepDistanceRemaining();

    public abstract Builder currentStep(LegStep currentStep);

    abstract LegStep currentStep();

    public abstract Builder currentStepPoints(List<Point> currentStepPoints);

    abstract List<Point> currentStepPoints();

    public abstract Builder upcomingStepPoints(@Nullable List<Point> upcomingStepPoints);

    abstract List<Point> upcomingStepPoints();

    public abstract Builder distanceRemaining(double distanceRemaining);

    abstract Builder currentLegProgress(RouteLegProgress routeLegProgress);

    public abstract Builder inTunnel(boolean inTunnel);

    public abstract Builder voiceInstruction(@Nullable VoiceInstruction voiceInstruction);

    public abstract Builder bannerInstruction(@Nullable BannerInstruction bannerInstruction);

    public abstract Builder currentState(@Nullable RouteProgressState currentState);

    public abstract Builder routeGeometryWithBuffer(@Nullable Geometry routeGeometryWithBuffer);

    abstract RouteProgress autoBuild(); // not public

    public RouteProgress build() {
      RouteLeg currentLeg = directionsRoute().legs().get(legIndex());
      RouteLegProgress legProgress = RouteLegProgress.builder()
        .routeLeg(currentLeg)
        .currentStep(currentStep())
        .stepIndex(stepIndex())
        .distanceRemaining(legDistanceRemaining())
        .durationRemaining(legDurationRemaining())
        .stepDistanceRemaining(stepDistanceRemaining())
        .currentStepPoints(currentStepPoints())
        .upcomingStepPoints(upcomingStepPoints())
        .build();
      currentLegProgress(legProgress);

      return autoBuild();
    }
  }

  public static Builder builder() {
    return new AutoValue_RouteProgress.Builder();
  }
}