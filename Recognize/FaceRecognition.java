/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Recognize;

import BuildEigenface.FaceBundle;
import BuildEigenface.FileUtils;
import BuildEigenface.ImageUtils;
import BuildEigenface.Matrix2D;
import inputan.mhs;
import inputan.mhsDao;
import inputan.mhsImpl;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.JOptionPane;

/**
 *
 * @author Arpian Fahmi
 */
public class FaceRecognition {

    private static final float FACES_FRAC = 0.75f;
    // default fraction of eigenfaces used in a match
    private mhsImpl impl = new mhsImpl();
    private FaceBundle bundle = null;
    private double[][] weights = null;    // training image weights
    private int numEFs = 0;     // number of eigenfaces to be used in the recognition
    private int countEFs = 0;

    public FaceRecognition(int numEigenFaces) {
        bundle = FileUtils.readCache();
        if (bundle == null) {
            System.out.println("You must build an Eigenfaces cache before any matching");
            System.exit(1);
        }

        int numFaces = bundle.getNumEigenFaces();
        countEFs = numEigenFaces;
        System.out.println("No of eigenFaces: " + numFaces);

        numEFs = numEigenFaces;
        if ((numEFs < 1) || (numEFs > numFaces - 1)) {
            numEFs = Math.round((numFaces - 1) * FACES_FRAC);     // set to less than max
            System.out.println("Number of matching eigenfaces must be in the range (1-"
                    + (numFaces - 1) + ")" + "; using " + numEFs);
        } else {
            System.out.println("Number of eigenfaces: " + numEFs);
        }

        weights = bundle.calcWeights(numEFs);
    }  // end of FaceRecognition()

    public MatchResult match(String imFnm) // match image in file against training images
    {
        if (!imFnm.endsWith(".png")) {
            System.out.println("Input image must be a PNG file");
            return null;
        } else {
            System.out.println("Matching " + imFnm);
        }

        BufferedImage image = FileUtils.loadImage(imFnm);
        if (image == null) {
            return null;
        }

        return match(image);
    }  // end of match() using filename

    public MatchResult match(BufferedImage im) // match loaded image against training images
    {
        if (bundle == null) {
            System.out.println("You must build an Eigenfaces cache before any matching");
            return null;
        }

        return findMatch(im);   // no checking of image size or grayscale
    }	  // end of match() using BufferedImage

    // ----------------- find matching results -----------------
    private MatchResult findMatch(BufferedImage im) {
        System.out.println(im);
        double[] imArr = ImageUtils.createArrFromIm(im);    // change image into an array

        // convert array to normalized 1D matrix
        Matrix2D imMat = new Matrix2D(imArr, 1);
        imMat.normalise();

        imMat.subtract(new Matrix2D(bundle.getAvgImage(), 1));  // subtract mean image
        Matrix2D imWeights = getImageWeights(numEFs, imMat);
        // map image into eigenspace, returning its coordinates (weights);
        // limit mapping to use only numEFs eigenfaces

        double[] dists = getDists(imWeights);
        ImageDistanceInfo distInfo = getMinDistInfo(dists);
        // find smallest Euclidian distance between image and training image

        ArrayList<String> imageFNms = bundle.getImageFnms();
        ArrayList<String> imageID = bundle.getId_mahasiswa();
        String matchingFNm = imageFNms.get(distInfo.getIndex());
        String matchingID = imageID.get(distInfo.getIndex());
        // get the training image filename that is closest 

        double minDist = Math.sqrt(distInfo.getValue());

        return new MatchResult(matchingFNm, minDist, matchingID);
    } // end of findMatch()

    private Matrix2D getImageWeights(int numEFs, Matrix2D imMat) /* map image onto numEFs eigenfaces returning its weights 
     (i.e. its coordinates in eigenspace)
     */ {
        Matrix2D egFacesMat = new Matrix2D(bundle.getEigenFaces());
        Matrix2D egFacesMatPart = egFacesMat.getSubMatrix(numEFs);
        Matrix2D egFacesMatPartTr = egFacesMatPart.transpose();

        return imMat.multiply(egFacesMatPartTr);
    }  // end of getImageWeights()

    private double[] getDists(Matrix2D imWeights) /* return an array of the sum of the squared Euclidian distance
     between the input image weights and all the training image weights */ {
        Matrix2D tempWt = new Matrix2D(weights);   // training image weights
        double[] wts = imWeights.flatten();

        tempWt.subtractFromEachRow(wts);
        tempWt.multiplyElementWise(tempWt);
        double[][] sqrWDiffs = tempWt.toArray();
        double[] dists = new double[sqrWDiffs.length];

        for (int row = 0; row < sqrWDiffs.length; row++) {
            double sum = 0.0;
            for (int col = 0; col < sqrWDiffs[0].length; col++) {
                sum += sqrWDiffs[row][col];
            }
            dists[row] = sum;
        }
        return dists;
    }  // end of getDists()

    private ImageDistanceInfo getMinDistInfo(double[] dists) {
        System.out.println("==================================");
        System.out.println("==================================");
        System.out.println("==================================");
        double minDist = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < dists.length; i++) {
            if (dists[i] < minDist) {
                minDist = dists[i];
                index = i;
            }
        }
        return new ImageDistanceInfo(dists[index], index);
    }	  // end of getMinDistInfo()

    public mhs RecognizingFace(BufferedImage faceImage) {
        long startTime = System.currentTimeMillis();

            FaceRecognition fr = new FaceRecognition(numEFs);
            MatchResult result = fr.match(faceImage);

            if (result == null) {
                System.out.println("No match found");
            } else {
                System.out.println();
                System.out.print("Matches image in " + result.getMatchFileName());
                System.out.printf("; distance = %.6f\n", result.getMatchDistance());

                mhs m = impl.get_mahasiswa(result.getName());
                m.setNim(result.getName());
                m.setImagePath(result.getMatchFileName());
                return m;
//                Tampung Hasil Pencarian dari masing-masing eigenfaces
//                if (mhs_s.containsKey(m.getNim())) {
//                    m = (mhs) mhs_s.get(m.getNim());
//                    m.setCountResult(m.getCountResult() + 1);
//                }
//                mhs_s.put(m.getNim(), m);
//
////                Cek hasil pencarian yang sudah ditampung
//                int terbanyak = 0;
//                for (Object key_1 : mhs_s.keySet()) {
//                    mhs m_1 = (mhs) mhs_s.get(key_1);
//                    if (terbanyak < m_1.getCountResult()) {
//                        terbanyak = m_1.getCountResult();
//                        foundKey = m_1.getNim();
//                    }
//                }
            }
        

        return null ;
    }
}
