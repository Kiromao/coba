/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PCA;

/**
 *
 * @author Arpian Fahmi
 */
public class pca {

    double trainingImage1[][] = {
        {2, 1, 1},
        {1, 2, 1},
        {1, 1, 2}
    };

    double trainingImage2[][] = {
        {1, 2, 1},
        {1, 2, 1},
        {1, 2, 1}
    };

    double trainingImage3[][] = {
        {1, 1, 2},
        {1, 2, 1},
        {2, 1, 1}
    };
    double trainingImage[][][] = {
        trainingImage1,
        trainingImage2,
        trainingImage3
    };

    public double[][] TheMean(double[][][] trainingImage) {
        double[][] temp = new double[trainingImage[0].length][trainingImage[0][0].length];
        double[][] mean = new double[trainingImage[0].length][trainingImage[0][0].length];

        for (int i = 0; i < trainingImage.length; i++) {
            for (int ii = 0; ii < trainingImage[i].length; ii++) {
                for (int iii = 0; iii < trainingImage[ii].length; iii++) {
                    temp[ii][iii] += trainingImage[i][ii][iii];
                }
            }
        }

        for (int i = 0; i < temp.length; i++) {
            for (int ii = 0; ii < temp[i].length; ii++) {
                mean[i][ii] = temp[i][ii] / trainingImage.length;
            }
        }
        return mean;
    }

    public double[][][] substractTheTiWithTheMean(double[][] mean, double[][][] ti) {
        for (int i = 0; i < ti.length; i++) {
            for (int j = 0; j < ti[i].length; j++) {
                for (int k = 0; k < ti[i][j].length; k++) {
                    ti[i][j][k] -= mean[j][k];
                }
            }
        }

        return ti;
    }

    public void PrintMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int ii = 0; ii < matrix[i].length; ii++) {
                System.out.print(matrix[i][ii] + " | ");
            }
            System.out.println("");
        }
    }

    public void PrintMatrix(double[][][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                for (int k = 0; k < matrix[i][j].length; k++) {
                    System.out.print(matrix[i][j][k] + " | ");
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        pca p = new pca();
        double[][] mean = p.TheMean(p.trainingImage);

        System.out.println("============MATRIX==============");
        p.PrintMatrix(p.trainingImage);
        System.out.println("============MEAN===================");
        p.PrintMatrix(mean);
        System.out.println("============Subtract Mean===========");
        p.PrintMatrix(p.substractTheTiWithTheMean(mean, p.trainingImage));
    }
}
