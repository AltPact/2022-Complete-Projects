import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * Author: Ashley Arik
 * Student Number: 850904
 * **/

public class Main extends Application {
    short[][][] cthead; // store the 3D volume data set
    short min, max; // min/max value in the 3D volume data set
    int CT_x_axis = 256;
    int CT_y_axis = 256;
    int CT_z_axis = 113;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
        stage.setTitle("CThead Viewer");

        ReadData();

        // Good practice: Define your top view, front view and side view images (get the height and width correct)
        // Here's the top view - looking down on the top of the head (each slice we are looking at is CT_x_axis x CT_y_axis)
        int topWidth = CT_x_axis;
        int topHeight = CT_y_axis;
        int topDepth = CT_z_axis;

        // Here's the front view - looking at the front (nose) of the head (each slice we are looking at is CT_x_axis x CT_z_axis)
        int frontWidth = CT_x_axis;
        int frontHeight = CT_z_axis;
        int frontDepth = CT_y_axis;

        // and you do the other (side view) - looking at the ear of the head
        int sideWidth = CT_y_axis;
        int sideHeight = CT_z_axis;
        int sideDepth = CT_x_axis;

        // We need 3 things to see an image
        // 1. We create an image we can write to
        WritableImage topImage = new WritableImage(topWidth, topHeight); //Image of slice from the Top
        WritableImage frontImage = new WritableImage(frontWidth, frontHeight); ////Image of slice from the Front
        WritableImage sideImage = new WritableImage(sideWidth, sideHeight); //Image of slice from the Side

        // 2. We create a view of that image that will be placed on the Window
        ImageView topView = new ImageView(topImage);
        ImageView frontView = new ImageView(frontImage);
        ImageView sideView = new ImageView(sideImage);

        // Creates the button that will be used as short cuts to off the volume and depth based Renders
        // Volume Render will show off the answer to 2a
        Button slice76Button = new Button("slice76"); // an example button to get the slice 76
        Button volumeRending = new Button("Volume Render");
        Button depthBasedRending = new Button("Depth Render");


        /**This will create the sliders
         * "Topslider", "FrontSlider" and "SideSlider" are used to more through the slides
         * "TopSliderOpacity", "FrontSliderOpacity" and "SideSliderOpacity" change the opacity of the skin from 0 to 1
         *  Attribute changed and there meaning (proof I know what I changing):
         *      setBlockIncrement - Set what the smallest amount a slider value can be changed
         *      setMajorTickUnit - Sets amount inbetween marked Value on the slider scale
         *      setShowTickLabels - Show scale Values
         *      setShowTickMarks - Show scale line marks
         *      setPadding - Set empty space around slider
         *      setLayoutX/setLayoutY - Movement of the slider in the x/y axis that is added
         * **/
        Slider topSlider = new Slider(0, topDepth - 1, 1);
        topSlider.setBlockIncrement(1); topSlider.setMajorTickUnit(20.0);
        topSlider.setOrientation(Orientation.VERTICAL); topSlider.setShowTickLabels(true); topSlider.setShowTickMarks(true);
        topSlider.setPadding(new Insets(0.0,10.0,0.0,10.0));

        Slider topSliderOpacity = new Slider(0, 1, 0);
        topSliderOpacity.setBlockIncrement(0.01); topSliderOpacity.setLayoutX(267.0); topSliderOpacity.setLayoutY(10);
        topSliderOpacity.setMajorTickUnit(0.1); topSliderOpacity.setOrientation(Orientation.VERTICAL); topSliderOpacity.setShowTickLabels(true); topSliderOpacity.setShowTickMarks(true);
        topSliderOpacity.setPadding(new Insets(0.0,15.0,0.0,15.0));

        Slider frontSlider = new Slider(0, frontDepth - 1 , 1);
        frontSlider.setBlockIncrement(1); frontSlider.setMajorTickUnit(20.0);
        frontSlider.setOrientation(Orientation.VERTICAL); frontSlider.setShowTickLabels(true); frontSlider.setShowTickMarks(true);
        frontSlider.setPadding(new Insets(0.0,10.0,0.0,10.0));

        Slider frontSliderOpacity = new Slider(0, 1,0);
        frontSliderOpacity.setBlockIncrement(0.01); frontSliderOpacity.setLayoutX(267.0); frontSliderOpacity.setLayoutY(10);
        frontSliderOpacity.setMajorTickUnit(0.1); frontSliderOpacity.setOrientation(Orientation.VERTICAL); frontSliderOpacity.setShowTickLabels(true); frontSliderOpacity.setShowTickMarks(true);
        frontSliderOpacity.setPadding(new Insets(0.0,15.0,0.0,15.0));

        Slider sideSlider = new Slider(0, sideDepth - 1, 1);
        sideSlider.setBlockIncrement(1); sideSlider.setMajorTickUnit(20.0);
        sideSlider.setOrientation(Orientation.VERTICAL); sideSlider.setShowTickLabels(true); sideSlider.setShowTickMarks(true);
        sideSlider.setPadding(new Insets(0.0,10.0,0.0,10.0));

        Slider sideSliderOpacity = new Slider(0, 1, 0);
        sideSliderOpacity.setBlockIncrement(0.01);sideSliderOpacity.setMajorTickUnit(0.1); sideSliderOpacity.setOrientation(Orientation.VERTICAL);
        sideSliderOpacity.setShowTickLabels(true); sideSliderOpacity.setShowTickMarks(true);
        sideSliderOpacity.setPadding(new Insets(0.0,15.0,0.0,15.0));



        slice76Button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TopDownSlice76(topImage);
            }
        });

        //Button that creates a volume Render with the 2a Requirements
        volumeRending.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TopVolumeRender(topImage, topDepth, false, 0);
                SideVolumeRender(sideImage, sideDepth, false, 0);
                FrontVolumeRender(frontImage, frontDepth, false, 0);
            }
        });

        //Just added for fun
        depthBasedRending.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TopDepthRender(topImage, topDepth);
                SideDepthRender(sideImage, sideDepth);
                FrontDepthRender(frontImage, frontDepth);
            }
        });

        //Calls to create new image slide when changed (same for "sideSlider" and "frontSlider")
        topSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        System.out.println(newValue.intValue());
                        ImageShownTop(topImage, newValue.intValue());
                        //prints the new Image number
                    }
                });

        //Calls to create new volume Render Image when changed (same for "sideSliderOpacity" and "frontSliderOpacity")
        topSliderOpacity.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        TopVolumeRender(topImage, topDepth, true, (double) newValue);
                        //prints the new Image number
                    }
                });

        sideSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        System.out.println(newValue.intValue());
                        ImageShownSide(sideImage, newValue.intValue());
                        //prints the new Image number
                    }
                });

        sideSliderOpacity.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        SideVolumeRender(sideImage, sideDepth, true, (double) newValue);
                        //prints the new Image number
                    }
                });

        frontSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        System.out.println(newValue.intValue());
                        ImageShownFront(frontImage, newValue.intValue());
                        //prints the new Image number
                    }
                });

        frontSliderOpacity.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        FrontVolumeRender(frontImage, frontDepth, true, (double) newValue);
                        //prints the new Image number
                    }
                });

        /*
        HBox horizontalBox = new HBox();
        horizontalBox.setAlignment(Pos.CENTER);
        horizontalBox.getChildren().addAll();
         */

        /**SplitPanes contain the Header of Section on top. It also  holds represented Image and Sliders for the bottom section of it
         * Attribute changed and there meaning (proof I know what I changing):
         * setDividerPositions - sets the positions of the dividers between eachother (0.5 meaning there quite close together)
         * setPrefHeight/setPrefWitch - Sets the smallest the heights and width can be. (Default if there isn't enough room on the window)
         * setContentDisplay/setAligment/setTextAligment - Dictate that the box/textInsideBox will be center framed inside of the Window
         * **/
        SplitPane splitPaneTop = new SplitPane(); splitPaneTop.setDividerPositions(0.5); splitPaneTop.setOrientation(Orientation.VERTICAL); splitPaneTop.setPrefHeight(270.0); splitPaneTop.setPrefWidth(300.0);
            Label TopSliceHeader = new Label(); TopSliceHeader.setAlignment(Pos.CENTER); TopSliceHeader.setContentDisplay(ContentDisplay.CENTER); TopSliceHeader.setTextAlignment(TextAlignment.CENTER); TopSliceHeader.setText("Top Down Slices");
            HBox hBoxTopSlice = new HBox(); hBoxTopSlice.getChildren().addAll(topView, topSlider, topSliderOpacity);

        SplitPane splitPaneSide = new SplitPane(); splitPaneSide.setDividerPositions(0.5); splitPaneSide.setOrientation(Orientation.VERTICAL); splitPaneSide.setPrefHeight(270.0); splitPaneSide.setPrefWidth(300.0);
            Label sideSliceHeader = new Label(); sideSliceHeader.setAlignment(Pos.CENTER); sideSliceHeader.setContentDisplay(ContentDisplay.CENTER); sideSliceHeader.setTextAlignment(TextAlignment.CENTER); sideSliceHeader.setText("Side to Side Slices");
            HBox hBoxSideSlice = new HBox(); hBoxSideSlice.getChildren().addAll(sideView, sideSlider, sideSliderOpacity);
            //<Label alignment="CENTER" contentDisplay="CENTER" text="Side-to-side Slicing" textAlignment="CENTER" />
        //<SplitPane dividerPositions="0.5" layoutX="333.0" layoutY="10.0" orientation="VERTICAL" prefHeight="150.0" prefWidth="300.0">
        SplitPane splitPaneFront = new SplitPane(); splitPaneFront.setDividerPositions(0.5); splitPaneFront.setOrientation(Orientation.VERTICAL); splitPaneFront.setPrefHeight(270.0); splitPaneFront.setPrefWidth(300.0); //splitPaneFront.setLayoutX(300); splitPaneFront.setLayoutY(10);
            Label frontSliceHeader = new Label(); frontSliceHeader.setAlignment(Pos.CENTER); frontSliceHeader.setContentDisplay(ContentDisplay.CENTER); frontSliceHeader.setTextAlignment(TextAlignment.CENTER); frontSliceHeader.setText("Front Slices");
            HBox hBoxFrontSlice = new HBox(); hBoxFrontSlice.getChildren().addAll(frontView, frontSlider, frontSliderOpacity);

        SplitPane splitPaneButtons = new SplitPane(); splitPaneButtons.setDividerPositions(0.5); splitPaneButtons.setOrientation(Orientation.VERTICAL); splitPaneButtons.setPrefHeight(270.0); splitPaneButtons.setPrefWidth(300.0); //splitPaneFront.setLayoutX(300); splitPaneFront.setLayoutY(10);
        Label buttonHeader = new Label(); buttonHeader.setAlignment(Pos.CENTER); buttonHeader.setContentDisplay(ContentDisplay.CENTER); buttonHeader.setTextAlignment(TextAlignment.CENTER); buttonHeader.setText("Buttons");
        HBox hBoxButton = new HBox(); hBoxButton.getChildren().addAll(volumeRending, depthBasedRending, slice76Button);

        //Puts all the SplitPanes into one scene that can be displaced on the Window
        splitPaneTop.getItems().addAll(TopSliceHeader, hBoxTopSlice);
        splitPaneSide.getItems().addAll(sideSliceHeader, hBoxSideSlice);
        splitPaneFront.getItems().addAll(frontSliceHeader, hBoxFrontSlice);
        splitPaneButtons.getItems().addAll(buttonHeader, hBoxButton);

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        border.setTop(hbox);

        FlowPane CombineEverything = new FlowPane();
        CombineEverything.getChildren().addAll(splitPaneTop, splitPaneSide, splitPaneFront, splitPaneButtons);

        Scene scene = new Scene(CombineEverything, 700, 600);
        stage.setScene(scene);
        stage.show();
    }

    // Function to read in the cthead data set
    public void ReadData() throws IOException, URISyntaxException {
        //TO-DO: Have the user inputs name of the File they want to be read
        File file = new File(Main.class.getResource("CThead.CTSCAN").toURI());
        // Read the data quickly via a buffer (in C++ you can just do a single fread - I couldn't find if there is an equivalent in Java)
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

        int i, j, k; // loop through the 3D data set

        min = Short.MAX_VALUE;
        System.out.println("min: " + min);
        max = Short.MIN_VALUE; // set to extreme values
        System.out.println("max: " + max);
        short read; // value read in
        int b1, b2; // data is wrong Endian (check wikipedia) for Java so we need to swap the bytes around

        cthead = new short[CT_z_axis][CT_y_axis][CT_x_axis]; // allocate the memory - note this is fixed for this data set
        // loop through the data reading it in
        for (k = 0; k < CT_z_axis; k++) {
            for (j = 0; j < CT_y_axis; j++) {
                for (i = 0; i < CT_x_axis; i++) {
                    // because the Endianess is wrong, it needs to be read byte at a time and swapped
                    b1 = ((int) in.readByte()) & 0xff; // the 0xff is because Java does not have unsigned types
                    b2 = ((int) in.readByte()) & 0xff; // the 0xff is because Java does not have unsigned types
                    read = (short) ((b2 << 8) | b1); // and swizzle the bytes around
                    if (read < min) min = read; // update the minimum
                    if (read > max) max = read; // update the maximum
                    cthead[k][j][i] = read; // put the short into memory (in C++ you can replace all this code with one fread)
                }
            }
        }
        System.out.println("MIN: " + min);
        System.out.println("MAX: " + max);
        System.out.println(min + " " + max); //diagnostic - for CThead this should be -1117, 2248
        //(i.e. there are 3366 levels of grey (we are trying to display on 256 levels of grey)
        //therefore histogram equalization would be a good thing
        //maybe put your histogram equalization code here to set up the mapping array
    }

    /*
       This function shows how to carry out an operation on an image.
       It obtains the dimensions of the image, and then loops through
       the image carrying out the copying of a slice of data into the
       image.
   */
    //Create a volume Render from the Top Slices
    public void TopVolumeRender(WritableImage topImage, int topDepth, boolean slider, double sliderVal){
        int w = (int) topImage.getWidth(), h = (int) topImage.getHeight();
        int d = topDepth;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                HashMap<Integer, Short> depthLookup = new HashMap<>();
                for (int f = 0; f < d; f++){
                    depthLookup.put(f, cthead[f][j][i]);
                }
                VolumeRenderAdding(depthLookup, topDepth,  topImage, i, j, slider, sliderVal);
            } // column loop
        } // row loop
    }

    //Create a volume Render from the Side Slices
    public void SideVolumeRender(WritableImage sideImage, int sideDepth, boolean slider, double sliderVal){
        int w = (int) sideImage.getWidth(), h = (int) sideImage.getHeight();
        int d = sideDepth;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                HashMap<Integer, Short> depthLookup = new HashMap<>();
                for (int f = 0; f < d; f++){
                    depthLookup.put(f, cthead[j][i][f]);
                }
                VolumeRenderAdding(depthLookup, sideDepth,  sideImage, i, j, slider, sliderVal);
            } // column loop
        } // row loop
    }

    //Create a volume Render from the Fronts Slices
    public void FrontVolumeRender(WritableImage frontImage, int frontDepth, boolean slider, double sliderVal){
        int w = (int) frontImage.getWidth(), h = (int) frontImage.getHeight();
        int d = frontDepth;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                HashMap<Integer, Short> depthLookup = new HashMap<>();
                for (int f = 0; f < d; f++){
                    depthLookup.put(f, cthead[j][f][i]);
                }
                VolumeRenderAdding(depthLookup, frontDepth,  frontImage, i, j, slider, sliderVal);
            } // column loop
        } // row loop
    }

    public void TopDepthRender(WritableImage topImage, int topDepth){
        int w = (int) topImage.getWidth(), h = (int) topImage.getHeight();
        int d = topDepth;
        PixelWriter image_writer = topImage.getPixelWriter();
        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                short opacity = 0;
                List<Double> imageQuality = null;
                for (int f = 0; f < d; f++){
                    try {
                        datum = cthead[f][j][i];

                        if (datum > 400){
                            image_writer.setColor(i,j,Color.color( 1, 1, 1, (1-f/d)));
                            break;
                        }
                    } catch (Exception e){

                    }
                    if (f == d - 1){
                        image_writer.setColor(i,j,Color.color( 0, 0, 0, 1));
                        break;
                    }
                }
            } // column loop
        } // row loop
    }

    public void SideDepthRender(WritableImage sideImage, int sideDepth){
        int w = (int) sideImage.getWidth(), h = (int) sideImage.getHeight();
        int d = sideDepth;
        PixelWriter image_writer = sideImage.getPixelWriter();
        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                short opacity = 0;
                List<Double> imageQuality = null;
                for (int f = 0; f < d; f++){
                    try {
                        datum = cthead[j][f][i];

                        if (datum > 400){
                            image_writer.setColor(i,j,Color.color( 1, 1, 1, (1-f/d)));
                            break;
                        }
                    } catch (Exception e){

                    }
                    if (f == d - 1){
                        image_writer.setColor(i,j,Color.color( 0, 0, 0, 1));
                        break;
                    }
                }
            } // column loop
        } // row loop
    }

    public void FrontDepthRender(WritableImage frontImage, int frontDepth){
        int w = (int) frontImage.getWidth(), h = (int) frontImage.getHeight();
        int d = frontDepth;
        PixelWriter image_writer = frontImage.getPixelWriter();
        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                short opacity = 0;
                List<Double> imageQuality = null;
                for (int f = 0; f < d; f++){
                    try {
                        datum = cthead[j][i][f];
                        if (datum > 400){
                            image_writer.setColor(i,j,Color.color( 1, 1, 1, (1-f/d)));
                            break;
                        }
                    } catch (Exception e){

                    }
                    if (f == d - 1){
                        image_writer.setColor(i,j,Color.color( 0, 0, 0, 1));
                        break;
                    }

                }
            } // column loop
        } // row loop
    }

    //Determines the Transparency, final colour and opacity of Volume Rendered Pixels by looping through each slices of the point
    /**depthLoopUp - Stores each point hit by the ray
     * depthValue - Stores number of slices that are available to go through
     * image - the Image that is change
     * i and j - Height and width point in the Image
     * SliderUsed - True if this function is called with the slider, so the function knows to change the opacity of the skin
     * SliderVal - Opacity value the skin will be set too
     * **/
    public void VolumeRenderAdding(HashMap<Integer, Short> depthLookUp, int depthValue, WritableImage image, int i, int j, boolean sliderUsed, double sliderVal){
        List<Double> imageQuality = null;
        double transparency = 1;
        double colRed = 0.0;
        double colBlue = 0.0;
        double colGreen = 0.0;
        PixelWriter image_writer = image.getPixelWriter();
        for (int f = 0; f < depthValue; f++){
            imageQuality = TransferFunction(depthLookUp.get(f), sliderUsed, sliderVal);
            if (imageQuality.get(3) == 0){
            }
            else {
                colRed += transparency * imageQuality.get(3) * imageQuality.get(0);
                colGreen += transparency * imageQuality.get(3) * imageQuality.get(1);
                colBlue += transparency * imageQuality.get(3) * imageQuality.get(2);
                transparency = (transparency * (1 - imageQuality.get(3)));
            }
            if (transparency <= 0){
                //System.out.println("Depth: " + f);
                break;
            }
        }
        if (colRed > 1){ colRed = 1;}
        if (colGreen > 1){ colGreen = 1;}
        if (colBlue > 1){ colBlue = 1;}
        image_writer.setColor(i,j,Color.color(colRed, colGreen, colBlue, 1));
    }

    public void TopDownSlice76(WritableImage image) {
        // Get image dimensions, and declare loop variables
        int w = (int) image.getWidth(), h = (int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();

        double col;
        short datum;
        // Shows how to loop through each pixel and colour
        // Try to always use j for loops in y, and i for loops in x
        // as this makes the code more readable
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                datum = cthead[76][j][i];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
            } // column loop
        } // row loop
    }

    /**Same for ImageShownTop, ImageShownSide and ImageShownFront
     * Each method writes the new image based on the slide number (the "newImageValue")
     * "Image" is the Image that is being rewriten by the program
     * **/
    public void ImageShownTop(WritableImage image, int newImageValue) {
        int w = (int) image.getWidth(), h = (int) image.getHeight();
        int d = (int) CT_z_axis;
        PixelWriter image_writer = image.getPixelWriter();

        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                datum = cthead[newImageValue][j][i];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
            } // column loop
        } // row loop
    }

    public void ImageShownSide(WritableImage image, int newImageValue) {
        int w = (int) image.getWidth(), h = (int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();
        //System.out.println("Width: " + w);
        //System.out.println("Height: " + h);
        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                datum = cthead[j][i][newImageValue];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
            } // column loop
        } // row loop
    }

    public void ImageShownFront(WritableImage image, int newImageValue) {
        int w = (int) image.getWidth(), h = (int) image.getHeight();
        PixelWriter image_writer = image.getPixelWriter();

        double col;
        short datum;
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                datum = cthead[j][newImageValue][i];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
            } // column loop
        } // row loop
    }

    /**Determines the colour and opacity of each pointed hit by the ray
     * Datum - Value of the point detected by the ray
     * SliderUsed - Determines if the skin opacity needs to be changed based on how with function is called
     * SilderVal - Opacity of Skin based on the value on the slider
     * **/
    public List<Double> TransferFunction(short datum, boolean sliderUsed, double sliderVal){
        double opacity = 0;
        double colRed = 0;
        double colGreen = 0;
        double colBlue = 0;
        List<Double> imageList = new ArrayList<>();
        if (datum < -300){
            colRed = 0;
            colGreen = 0;
            colBlue = 0;
            opacity = 0;
        }
        else if ((-300 <= datum) && (datum <= 49)){
            colRed = 1.0;
            colGreen = 0.79;
            colBlue = 0.6;
            if (sliderUsed){
                opacity = sliderVal;
            }
            else{
                opacity = 0.12;
            }
        }
        else if ((50 <= datum) && (datum <= 299)){
            colRed = 0;
            colGreen = 0;
            colBlue = 0;
            opacity = 0;
        }
        else if ((300 <= datum) && (datum <= 4096)){
            colRed = 1.0;
            colGreen = 1.0;
            colBlue = 1.0;
            opacity = 0.8;
        }
        else {
            colRed = 0;
            colGreen = 0;
            colBlue = 0;
            opacity = 0;}
        imageList.add(colRed);
        imageList.add(colGreen);
        imageList.add(colBlue);
        imageList.add(opacity);
        return imageList;
    }

    private String[][] sortByScore(String[][] in) {
        String[][] out = Arrays.stream(in) //this uses java 8 streams and takes the in[][] which is in your case the array "arr"
                .sorted(Comparator.comparing(x -> -Integer.parseInt(x[1]))) //sorts it
                .toArray(String[][]::new); //puts it onto the out array

        return out; //and returns the out array back
    }

}