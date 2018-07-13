/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tracker;

import Image.trainingImage;
import Image.trainingImageImpl;
import Recognize.FaceRecognition;
import filter.Equalization;
import filter.Median;
import inputan.mhs;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author Arpian Fahmi
 */
public class FaceDetectionController {

    // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;

    // face cascade classifier
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;

    private JButton cameraButton;
    JButton btnSnap;
    JButton btnSave;
    JButton btnRecognize;

    private JPanel originalFrame;

    Mat currentFrame;
    Rect currentRectCrop;

    Utils utils = new Utils();
    FaceRecognition faceRecognition = new FaceRecognition(0);

    public FaceDetectionController(JButton cameraButton, JPanel originalFrame, JButton btnSnap, JButton btnSave, JButton btnRecognize) {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier(FaceDetection.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
        this.absoluteFaceSize = 0;

        this.cameraButton = cameraButton;
        this.btnSave = btnSave;
        this.btnSnap = btnSnap;
        this.btnRecognize = btnRecognize;
        this.originalFrame = originalFrame;
        this.originalFrame.setSize(500, 500);

    }

    protected void startCamera() {
        if (!this.cameraActive) {
            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run() {
                        // effectively grab and process a single frame
                        currentFrame = grabFrame();
                        // convert and show the frame
                        BufferedImage imageToShow = (BufferedImage) Utils.mat2Image(currentFrame);
                        updateImageView(originalFrame, imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
                this.cameraButton.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
            this.cameraButton.setText("Start Camera");
            // enable classifiers checkboxe
            // stop the timer
            this.stopAcquisition();
        }
        btnSnap.setEnabled(this.cameraActive);
    }

//  ambil frame dari kamera  
    private Mat grabFrame() {
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    // face detection
                    this.detectAndDisplay(frame);
                }

            } catch (Exception e) {
                // log the (full) error
                System.err.println("Exception during the image elaboration: " + e);
            }
        }
        return frame;
    }

//    deteksi wajah dan memberikan tanda kotak  hijau
    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);
        // compute minimum face size (20% of the frame height, in our case)
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(
                grayFrame,
                faces,
                1.1,
                2,
                0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize),
                new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();

//        btnSave.setEnabled(facesArray.length == 1);
        btnSnap.setEnabled(facesArray.length == 1);
        btnRecognize.setEnabled(facesArray.length == 1);

        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 100, 0), 2);
            currentRectCrop = new Rect(facesArray[i].tl(), facesArray[i].br());
        }
        if (facesArray.length == 1) {
//            RecognizeImage();
        }

    }

//    stop camera
    private void stopAcquisition() {
        if (this.timer != null && !this.timer.isShutdown()) {
            try {
                // stop the timer
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log any exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }
        }

        if (this.capture.isOpened()) {
            // release the camera
            this.capture.release();
        }
    }

//    tampilkan hasil dari kamera ke dalam panel
    private void updateImageView(JPanel view, BufferedImage image) {
//        Utils.onFXThread(view.imageProperty(), image);
        Graphics g = view.getGraphics();
        g.drawImage(image,
                0, 0,
                view.getWidth(), view.getHeight(), 0, 0,
                view.getWidth(), view.getHeight(), null);
    }

    public void SaveImage(String fnm, int image_num, int id_mhs) {
        utils.saveImage(fnm, image_num, id_mhs);
    }

    public void SnapImage(JLabel preview_hasil_snap) {
        utils.SnapImage(currentFrame, currentRectCrop, preview_hasil_snap);
    }

    public mhs RecognizeImage() {
       return faceRecognition.RecognizingFace(utils.RecognizeImage(currentFrame, currentRectCrop));
    }
    public void Absen(){
        
    }

    protected void setClosed() {
        this.stopAcquisition();
    }

}

class Utils {

    private static final String FILE_EXT = ".png";

    private static final String TRAINING_DIR = "trainingImages";

    private static final String EF_CACHE = "eigen.cache";

    private static final String EIGENFACES_DIR = "eigenfaces";
    private static final String EIGENFACES_PREFIX = "eigen_";

    private static final String RECON_DIR = "reconstructed";
    private static final String RECON_PREFIX = "recon_";

    private static final int FACE_WIDTH = 125;
    private static final int FACE_HEIGHT = 150;

    trainingImageImpl ti = new trainingImageImpl();

    List<BufferedImage> images = new ArrayList<>();
    
    Median median = new Median();
    Equalization equalization = new Equalization();

    public static Image mat2Image(Mat frame) {
        try {
            return matToBufferedImage(frame);
        } catch (Exception e) {
            System.err.println("Cannot convert the Mat object: " + e);
            return null;
        }
    }

//    matrix yang sudah diproses dikembalikan lagi menjadi gambar
    private static BufferedImage matToBufferedImage(Mat original) {
        // init
        BufferedImage image = null;

        int width = original.width(), height = original.height(), channels = original.channels();
        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }

//    Resize ukuran image yang diambil dari kamera 
    private BufferedImage resizeImage(BufferedImage im) // resize to at least a standard size, then convert to grayscale 
    {
        // resize the image so *at least* FACE_WIDTH*FACE_HEIGHT size
        int imWidth = im.getWidth();
        int imHeight = im.getHeight();
        System.out.println("Original (w,h): (" + imWidth + ", " + imHeight + ")");

        double widthScale = FACE_WIDTH / ((double) imWidth);
        double heightScale = FACE_HEIGHT / ((double) imHeight);
        double scale = (widthScale > heightScale) ? widthScale : heightScale;

        int nWidth = (int) Math.round(imWidth * scale);
        int nHeight = (int) Math.round(imHeight * scale);

        // convert to grayscale while resizing
        BufferedImage grayIm = new BufferedImage(nWidth, nHeight,
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2 = grayIm.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(im, 0, 0, nWidth, nHeight, 0, 0, imWidth, imHeight, null);
        g2.dispose();

        System.out.println("Scaled gray (w,h): (" + nWidth + ", " + nHeight + ")");
        return grayIm;
    }  // end of resizeImage()

    public void saveImage(String fnm, int image_num, int id_mhs) // save image in fnm
    {
        String name = null;
        try {
            for (BufferedImage image : images) {
                name = TRAINING_DIR + "/" + fnm + "_" + image_num + ".png";
                ImageIO.write(image, "png", new File(name));
                System.out.println("Saved image to " + name);

                trainingImage t_image = new trainingImage();
                t_image.setPath(fnm + "_" + image_num + ".png");
                mhs mhsiswa = new mhs();
                mhsiswa.setId(id_mhs);
                t_image.setMahasiswa(mhsiswa);

                ti.trainingImage(t_image);
                image_num++;
            }
            images.clear();
        } catch (IOException e) {
            System.out.println("Could not save image to " + name);
        }
    }  // end of saveImage()

    public void SnapImage(Mat frame, Rect rectCrop, JLabel preview_hasil_snap) {
        try {
            Mat image_roi = new Mat(frame, rectCrop);
            BufferedImage result_resize_image = resizeImage((BufferedImage) mat2Image(image_roi));

//            Median Filter
            result_resize_image = median.medianfilter(result_resize_image);
//            Histogtam
            result_resize_image = equalization.Equalization(result_resize_image);
            
            images.add(result_resize_image);

            BufferedImage prosesGambar = images.get(images.size() - 1);
            preview_hasil_snap.setIcon(new ImageIcon(prosesGambar));

            System.out.println("Image has been Take");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    mencocokan Image yang baru dengan yang ada di database
    public BufferedImage RecognizeImage(Mat frame, Rect rectCrop) {
        try {
            Mat image_roi = new Mat(frame, rectCrop);
            
            BufferedImage bi = resizeImage((BufferedImage)mat2Image(image_roi));
            
//            Median Filter
            bi = median.medianfilter(bi);
//            Histogtam
            bi = equalization.Equalization(bi);
            
            return bi;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return null;
    }
}
