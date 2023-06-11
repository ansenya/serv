package ru.senya.pixatekaserv.utils;


import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class Utils {

    public static final String PATH_FOLDER = "C:/projects/data/";
    public static String SERVER_IP;
    public static String SERVER_PORT;
    public static String SERVER_HOST;

    public static String getTags(String path) {
        Mat frame = new Mat(), frameResized = new Mat();
        float minProbability = 0.5f, threshold = 0.3f;
        int height, width;
        String cocoPath = "src/config/coco.names";
        List<String> labels = labels(cocoPath);


        String cfgPath = "src/config/yolov3.cfg";
        String weightsPath = "src/config/yolov3.weights";
        Net network = Dnn.readNetFromDarknet(cfgPath, weightsPath);
        network.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        List<String> namesOfAllLayers = network.getLayerNames();

        MatOfInt outputLayersIndexes = network.getUnconnectedOutLayers();
        int amountOfOutputLayers = outputLayersIndexes.toArray().length;

        List<String> outputLayersNames = new ArrayList<>();
        for (int i = 0; i < amountOfOutputLayers; i++) {
            outputLayersNames.add(namesOfAllLayers.get(outputLayersIndexes.toList().get(i) - 1));
        }


        frame = Imgcodecs.imread(path);
        height = frame.height();
        width = frame.width();

        Imgproc.resize(frame, frameResized, new Size(192, 192));
        Mat blob = Dnn.blobFromImage(frameResized, 1 / 255.0);
        network.setInput(blob);

        List<Mat> outputFromNetwork = new ArrayList();
        for (int i = 0; i < amountOfOutputLayers; i++) {
            outputFromNetwork.add(network.forward(outputLayersNames.get(i)));
        }

        List<Rect2d> boundingBoxesList = new ArrayList();
        MatOfRect2d boundingBoxes = new MatOfRect2d();

        List<Float> confidencesList = new ArrayList();
        MatOfFloat confidences = new MatOfFloat();

        List<Integer> classIndexes = new ArrayList();

        for (int i = 0; i < amountOfOutputLayers; i++) {
            for (int b = 0; b < outputFromNetwork.get(i).size().height; b++) {
                double[] scores = new double[labels.size()];
                for (int c = 0; c < labels.size(); c++) {
                    scores[c] = outputFromNetwork.get(i).get(b, c + 5)[0];
                }

                int indexOfMaxValue = 0;
                for (int c = 0; c < labels.size(); c++) {
                    indexOfMaxValue = (scores[c] > scores[indexOfMaxValue]) ? c : indexOfMaxValue;
                }

                Double maxProbability = scores[indexOfMaxValue];

                if (maxProbability > minProbability) {

                    double boxWidth = outputFromNetwork.get(i).get(b, 2)[0] * width;
                    double boxHeight = outputFromNetwork.get(i).get(b, 3)[0] * height;
                    Rect2d boxRect2d = new Rect2d(
                            (outputFromNetwork.get(i).get(b, 0)[0] * width) - (boxWidth / 2),
                            (outputFromNetwork.get(i).get(b, 1)[0] * height) - (boxHeight / 2),
                            boxWidth,
                            boxHeight
                    );
                    boundingBoxesList.add(boxRect2d);
                    confidencesList.add(maxProbability.floatValue());
                    classIndexes.add(indexOfMaxValue);
                }
            }
        }

        boundingBoxes.fromList(boundingBoxesList);
        confidences.fromList(confidencesList);

        MatOfInt indices = new MatOfInt();
        Dnn.NMSBoxes(boundingBoxes, confidences, minProbability, threshold, indices);
        Set<String> tags = new HashSet<>();
        if (indices.size().height > 0) {
            for (int i = 0; i < indices.toList().size(); i++) {
                int classIndex = classIndexes.get(indices.toList().get(i));
//               tags.add(labels.get(classIndex) + ": " + Float.toString(confidences.toList().get(i)));
                tags.add(labels.get(classIndex));
            }
        }
        return String.join(" ", tags);
    }

    public static String getColor(String path) {
        // Загрузка изображения
        Mat image = Imgcodecs.imread(path);

        // Вычисление среднего значения всех пикселей
        Scalar meanColor = Core.mean(image);

        // Получение среднего цвета в формате BGR
        double blue = meanColor.val[0];
        double green = meanColor.val[1];
        double red = meanColor.val[2];

        // Преобразование значения цвета в формат HEX
        String hexColor = String.format("#%02X%02X%02X", (int) red, (int) green, (int) blue);

        // Вывод среднего цвета
//        System.out.println("Average Color: " + hexColor);
        return hexColor;
    }

    public static List<String> labels(String path) {
        List<String> labels = new ArrayList<>();
        try {
            Scanner scnLabels = new Scanner(new File(path));
            while (scnLabels.hasNext()) {
                String label = scnLabels.nextLine();
                labels.add(label);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return labels;
    }

}
