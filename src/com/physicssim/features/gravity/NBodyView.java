package com.physicssim.features.gravity;

import com.physicssim.model.gravity.NBodyModel;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;

/**
 * Main view for the N-Body gravity simulation.
 * Orchestrates the model, canvas, and control panel, handling all interactions.
 */
public class NBodyView extends BorderPane {

    private NBodyModel model;
    private NBodyCanvas canvas;
    private NBodyControlPanel controls;
    private AnimationTimer animationTimer;
    private long previousTime = 0;

    // Canvas dimensions
    private static final double CANVAS_WIDTH = 1100;
    private static final double CANVAS_HEIGHT = 700;

    // Circle arrangement parameter
    private double currentRadius = 150.0;

    public NBodyView() {
        // Initialize components
        model = new NBodyModel();
        canvas = new NBodyCanvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        controls = new NBodyControlPanel();

        // Setup layout
        setupLayout();

        // Configure event listeners
        configureControls();

        // Initialize with default bodies
        initializeDefaultBodies();

        // Start animation loop
        startAnimation();
    }

    /**
     * Sets up the main layout with canvas and control panel.
     */
    private void setupLayout() {
        setStyle("-fx-background-color: #0a0a0a;");

        // Canvas in center
        setCenter(canvas);

        // Control panel in right with scrolling support
        ScrollPane scrollPane = new ScrollPane(controls);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(250);
        scrollPane.setStyle("-fx-background-color: #1a1a1a;");
        setRight(scrollPane);
    }

    /**
     * Initializes simulation with default bodies in circular arrangement.
     */
    private void initializeDefaultBodies() {
        int bodyCount = 15;
        model.initializeCircularArrangement(
                bodyCount,
                CANVAS_WIDTH / 2,      // Center X
                CANVAS_HEIGHT / 2,     // Center Y
                currentRadius,         // Current configurable radius
                15                     // Orbital speed
        );
    }

    /**
     * Configures all control panel event listeners.
     */
    private void configureControls() {
        // Gravity slider
        controls.getGravitySlider().valueProperty().addListener(
                (obs, oldVal, newVal) -> {
                    model.setGravitationalConstant(newVal.doubleValue());
                    controls.getGravityValueLabel().setText(
                            String.format("%.1f", newVal.doubleValue())
                    );
                }
        );

        // Speed slider
        controls.getSpeedSlider().valueProperty().addListener(
                (obs, oldVal, newVal) -> {
                    model.setSimulationSpeed(newVal.doubleValue());
                    controls.getSpeedValueLabel().setText(
                            String.format("%.2fx", newVal.doubleValue())
                    );
                }
        );

        // Circle radius slider
        controls.getRadiusSlider().valueProperty().addListener(
                (obs, oldVal, newVal) -> {
                    currentRadius = newVal.doubleValue();
                    controls.getRadiusValueLabel().setText(
                            String.format("%.0f", newVal.doubleValue())
                    );
                    // Dynamically rearrange bodies with new radius if any exist
                    int bodyCount = model.getBodyCount();
                    if (bodyCount > 0) {
                        model.initializeCircularArrangement(
                                bodyCount,
                                CANVAS_WIDTH / 2,
                                CANVAS_HEIGHT / 2,
                                currentRadius,
                                12
                        );
                    }
                }
        );

        // Body size slider
        controls.getBodySizeSlider().valueProperty().addListener(
                (obs, oldVal, newVal) -> {
                    canvas.setBodySize(newVal.doubleValue());
                    controls.getBodySizeValueLabel().setText(
                            String.format("%.1f", newVal.doubleValue())
                    );
                }
        );

        // Initialize bodies button
        controls.getAddBodiesButton().setOnAction(e -> {
            int bodyCount = controls.getBodyCountSpinner().getValue();
            model.initializeCircularArrangement(
                    bodyCount,
                    CANVAS_WIDTH / 2,
                    CANVAS_HEIGHT / 2,
                    currentRadius,
                    12
            );
        });

        // Circular arrangement button
        controls.getCircularButton().setOnAction(e -> {
            int bodyCount = model.getBodyCount();
            if (bodyCount > 0) {
                model.initializeCircularArrangement(
                        bodyCount,
                        CANVAS_WIDTH / 2,
                        CANVAS_HEIGHT / 2,
                        currentRadius,
                        12
                );
            }
        });

        // Random arrangement button
        controls.getRandomButton().setOnAction(e -> {
            int bodyCount = model.getBodyCount();
            if (bodyCount > 0) {
                model.initializeRandomArrangement(
                        bodyCount,
                        CANVAS_WIDTH,
                        CANVAS_HEIGHT
                );
            }
        });

        // Clear trails button
        controls.getClearTrailsButton().setOnAction(e -> {
            model.clearTrails();
        });

        // Reset button
        controls.getResetButton().setOnAction(e -> {
            previousTime = 0;
            model.clear();
            int defaultCount = 15;
            model.initializeCircularArrangement(
                    defaultCount,
                    CANVAS_WIDTH / 2,
                    CANVAS_HEIGHT / 2,
                    currentRadius,
                    15
            );
            controls.getBodyCountSpinner().getValueFactory().setValue(defaultCount);
        });

        // Trail visibility checkbox
        controls.getShowTrailsCheckBox().selectedProperty().addListener(
                (obs, oldVal, newVal) -> canvas.setShowTrails(newVal)
        );

        // Velocity vector visibility checkbox
        controls.getShowVelocityCheckBox().selectedProperty().addListener(
                (obs, oldVal, newVal) -> canvas.setShowVelocityVectors(newVal)
        );
    }

    /**
     * Starts the animation loop using JavaFX AnimationTimer.
     * Uses adaptive timestep based on frame time.
     */
    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // First frame initialization
                if (previousTime == 0) {
                    previousTime = now;
                    return;
                }

                // Calculate delta time in seconds
                double dt = (now - previousTime) / 1_000_000_000.0;
                
                // Clamp dt to prevent instability (cap at 33ms = ~30fps)
                dt = Math.min(dt, 0.033);
                
                previousTime = now;

                // Update physics simulation
                model.update(dt);

                // Render updated state
                canvas.render(model.getBodies());
            }
        };

        animationTimer.start();
    }

    /**
     * Stops the animation loop.
     */
    public void stop() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }

    // ===== Getters =====

    public NBodyModel getModel() {
        return model;
    }

    public NBodyCanvas getCanvas() {
        return canvas;
    }

    public NBodyControlPanel getControls() {
        return controls;
    }
}