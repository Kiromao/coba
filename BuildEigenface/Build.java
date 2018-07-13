/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BuildEigenface;

import Image.trainingImageImpl;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class Build {

    static int n = 0;
    trainingImageImpl impl = new trainingImageImpl();

    public Build(int numEFs) // create a FaceBundle for the specified number of eigenfaces, and store it
    {
        ArrayList a = (ArrayList) impl.FileNamesAndID().get(0);
        if (a.size() > 0) {

            ArrayList<String> fnms = FileUtils.getTrainingFnms((ArrayList) impl.FileNamesAndID().get(0));
            ArrayList<String> id_mhs = (ArrayList) impl.FileNamesAndID().get(1);

            int numIms = fnms.size();
            if ((numEFs < 1) || (numEFs >= numIms)) {
                System.out.println("Number of eigenfaces must be in range (1-" + (numIms - 1) + ")"
                        + "; using " + (numIms - 1));
                numEFs = numIms - 1;
            } else {
                System.out.println("Number of eigenfaces: " + numEFs);
            }

            FaceBundle bundle = makeBundle(fnms, id_mhs);
            FileUtils.writeCache(bundle);
            numEFs = n;
            reconstructIms(numEFs, bundle);  // optional: rebuild the original images from the bundle
        }
    }

    private static FaceBundle makeBundle(ArrayList<String> fnms, ArrayList<String> id_mhs) // create eigenvectors/eigenvalue bundle for the specified training image filenames;
    // also save each eigenface (eigenvector) as an image file
    {
        BufferedImage[] ims = FileUtils.loadTrainingIms(fnms);

        for (String id : id_mhs) {
            System.out.println(id);
        }

        Matrix2D imsMat = convertToNormMat(ims);   // each row is a normalized image
        double[] avgImage = imsMat.getAverageOfEachColumn();
        imsMat.subtractMean();   // subtract mean face from each image (row)
        // each row now contains only distinguishing features from a training image 

        // calculate covariance matrix
        Matrix2D imsDataTr = imsMat.transpose();
        Matrix2D covarMat = imsMat.multiply(imsDataTr);

        // calculate Eigenvalues and Eigenvectors for covariance matrix
        EigenvalueDecomp egValDecomp = covarMat.getEigenvalueDecomp();
        double[] egVals = egValDecomp.getEigenValues();
        double[][] egVecs = egValDecomp.getEigenVectors();

        System.out.println("================EVe sebelum Sort===========================");
        for (double ds : egVals) {
            System.out.println(ds);
        }

        System.out.println("================EVa sebelum Sort===========================");
        for (double[] ds : egVecs) {
            for (double dss : ds) {
                System.out.print(dss + "   |  ");
            }
            System.out.println("");
        }
        sortEigenInfo(egVals, egVecs);   // sort Eigenvectos and Eigenvariables
        System.out.println("=================EV setelah Sort==========================");
        for (double ds : egVals) {
            System.out.println(ds);
        }
        System.out.println("================EVa sebelum Sort===========================");
        for (double[] ds : egVecs) {
            for (double dss : ds) {
                System.out.print(dss + "   |  ");
            }
            System.out.println("");
        }
        Matrix2D egFaces = getNormEgFaces(imsMat, new Matrix2D(egVecs));

        System.out.println("\nSaving Eigenfaces as images...");
        FileUtils.saveEFIms(egFaces, ims[0].getWidth());
        System.out.println("Saving done\n");

        return new FaceBundle(fnms, id_mhs, imsMat.toArray(), avgImage,
                egFaces.toArray(), egVals, ims[0].getWidth(), ims[0].getHeight());
    }  // end of makeBundle()

    private static Matrix2D convertToNormMat(BufferedImage[] ims) /* convert array of  images into a matrix; each row is an image
     and the number of columns is the number of pixels in the image.
     The array is normalized.
     */ {
        int imWidth = ims[0].getWidth();
        int imHeight = ims[0].getHeight();

        int numRows = ims.length;
        int numCols = imWidth * imHeight;
        double[][] data = new double[numRows][numCols];
        for (int i = 0; i < numRows; i++) {
            ims[i].getData().getPixels(0, 0, imWidth, imHeight, data[i]);    // one image per row
        }
        Matrix2D imsMat = new Matrix2D(data);
        imsMat.normalise();
        return imsMat;
    }		// end of convertToNormMat()

    private static Matrix2D getNormEgFaces(Matrix2D imsMat, Matrix2D egVecs) /* calculate normalized Eigenfaces for the training images by multiplying the 
     eigenvectors to the training images matrix */ {
        Matrix2D egVecsTr = egVecs.transpose();
        Matrix2D egFaces = egVecsTr.multiply(imsMat);
        double[][] egFacesData = egFaces.toArray();

        for (int row = 0; row < egFacesData.length; row++) {
            double norm = Matrix2D.norm(egFacesData[row]);   // get normal
            for (int col = 0; col < egFacesData[row].length; col++) {
                egFacesData[row][col] = egFacesData[row][col] / norm;
            }
        }
        return new Matrix2D(egFacesData);
    }  // end of getNormEgFaces()

    // ---------------------- sort the EigenVectors --------------------------
    private static void sortEigenInfo(double[] egVals, double[][] egVecs) /* sort the Eigenvalues and Eigenvectors arrays into descending order
     by eigenvalue. Add them to a table so the sorting of the values adjusts the
     corresponding vectors
     */ {
        Double[] egDvals = getEgValsAsDoubles(egVals);

        // create table whose key == eigenvalue; value == eigenvector
        Hashtable<Double, double[]> table = new Hashtable<Double, double[]>();
        for (int i = 0; i < egDvals.length; i++) {
            table.put(egDvals[i], getColumn(egVecs, i));
//            System.out.println(egDvals[i] + " | " + getColumn(egVecs, i));
        }

        ArrayList<Double> sortedKeyList = sortKeysDescending(table);
        updateEgVecs(egVecs, table, egDvals, sortedKeyList);
        // use the sorted key list to update the Eigenvectors array

        // convert the sorted key list into an array
        Double[] sortedKeys = new Double[sortedKeyList.size()];
        sortedKeyList.toArray(sortedKeys);

        // use the sorted keys array to update the Eigenvalues array
        for (int i = 0; i < sortedKeys.length; i++) {
            egVals[i] = sortedKeys[i].doubleValue();
        }
        n = egVals.length - 1;

    }  // end of sortEigenInfo()

    private static Double[] getEgValsAsDoubles(double[] egVals) // convert double Eigenvalues to Double objects, suitable for Hashtable keys
    {
        Double[] egDvals = new Double[egVals.length];
        for (int i = 0; i < egVals.length; i++) {
            egDvals[i] = new Double(egVals[i]);
        }
        return egDvals;
    }  // end of getEgValsAsDoubles()

    private static double[] getColumn(double[][] vecs, int col) /* the Eigenvectors array is in column order (one vector per column);
     return the vector in column col */ {
        double[] res = new double[vecs.length];
        for (int i = 0; i < vecs.length; i++) {
            res[i] = vecs[i][col];
        }
        return res;
    }  // end of getColumn()

    private static ArrayList<Double> sortKeysDescending(
            Hashtable<Double, double[]> table) // sort the keylist part of the hashtable into descending order
    {
        ArrayList<Double> keyList = Collections.list(table.keys());
        Collections.sort(keyList, Collections.reverseOrder()); // largest first
        return keyList;
    }  // end of sortKeysDescending()

    private static void updateEgVecs(double[][] egVecs,
            Hashtable<Double, double[]> table,
            Double[] egDvals, ArrayList<Double> sortedKeyList) /* get vectors from the table in descending order of sorted key,
     and update the original vectors array */ {
        for (int col = 0; col < egDvals.length; col++) {
            double[] egVec = table.get(sortedKeyList.get(col));
            for (int row = 0; row < egVec.length; row++) {
                egVecs[row][col] = egVec[row];
            }
        }
    }  // end of updateEgVecs()

    // ---------- reconstruction of images from eigenfaces ------------------
    private static void reconstructIms(int numEFs, FaceBundle bundle) {
        System.out.println("\nReconstructing training images...");

        Matrix2D egFacesMat = new Matrix2D(bundle.getEigenFaces());
        Matrix2D egFacesSubMat = egFacesMat.getSubMatrix(numEFs);

        Matrix2D egValsMat = new Matrix2D(bundle.getEigenValues(), 1);
        Matrix2D egValsSubMat = egValsMat.transpose().getSubMatrix(numEFs);

        double[][] weights = bundle.calcWeights(numEFs);
        double[][] normImgs = getNormImages(weights, egFacesSubMat, egValsSubMat);
        // the mean-subtracted (normalized) training images
        double[][] origImages = addAvgImage(normImgs, bundle.getAvgImage());
        // original training images = normalized images + average image

        FileUtils.saveReconIms2(origImages, bundle.getImageWidth());
        System.out.println("Reconstruction done\n");
    }  // end of reconstructIms()

    private static double[][] getNormImages(double[][] weights,
            Matrix2D egFacesSubMat, Matrix2D egValsSubMat) /* calculate weights x eigenfaces, which generates mean-normalized traimning images;
     there is one image per row in the returned array
     */ {
        double[] egDValsSub = egValsSubMat.flatten();
        Matrix2D tempEvalsMat = new Matrix2D(weights.length, egDValsSub.length);
        tempEvalsMat.replaceRowsWithArray(egDValsSub);

        Matrix2D tempMat = new Matrix2D(weights);
        tempMat.multiplyElementWise(tempEvalsMat);

        Matrix2D normImgsMat = tempMat.multiply(egFacesSubMat);
        return normImgsMat.toArray();
    }  // end of getNormImages()

    private static double[][] addAvgImage(double[][] normImgs, double[] avgImage) // add the average image to each normalized image (each row) and store in a new array;
    // the result are the original training images; one per row
    {
        double[][] origImages = new double[normImgs.length][normImgs[0].length];
        for (int i = 0; i < normImgs.length; i++) {
            for (int j = 0; j < normImgs[i].length; j++) {
                origImages[i][j] = normImgs[i][j] + avgImage[j];
            }
        }
        return origImages;
    }  // end of addAvgImage()
}
