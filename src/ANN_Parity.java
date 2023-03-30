import java.util.*;
import java.lang.*;

public class ANN_Parity {
    public int[] parity;
    public String[] data;
    public double[] hidden, W2, input;
    public double[][] W1;
    public double output;

    public ANN_Parity(){
        data = Data_Gen();
        parity = Parity_Gen(data);
        hidden = new double[8];
        input = new double[4];
        W1 = new double[8][4];
        W2 = new double[8];
        W1 = W1_Init(W1);
        W2 = W2_Init(W2);
        int epochs = 1000000;
        double L_rate = 0.5;
        int i;
        String dataIn;


        for (i=0;i<epochs;i++){
            int to_use = (int)(Math.random() *15);
            dataIn = data[to_use];
            input[0] = Character.getNumericValue(dataIn.charAt(0));
            input[1] = Character.getNumericValue(dataIn.charAt(1));
            input[1] = Character.getNumericValue(dataIn.charAt(2));
            input[3] = Character.getNumericValue(dataIn.charAt(3));
            Hidden_Calc(W1,input,hidden);
            output = Output_Calc(W2,hidden);
            if (i%5000 == 0) {
                System.out.println(Cost_Calc(output,parity[to_use]));
                System.out.println(output);
                System.out.println(parity[to_use]);
                System.out.println();
            };
            Gradient_Calc(W1,W2,hidden,input,output,parity[to_use],L_rate);
        }

    }

    void Gradient_Calc(double[][] w1, double[] w2, double[] hiddenL,double[] ins, double out, double actual,double l_rate){
        double[] delta3 = new double[w2.length];
        int i,j;
        for (i = 0;i<w2.length;i++){
            delta3[i] = w2[i]*2*(Act_Func(out)-actual)*Act_Func_Prime(out);
        }
        double[][] delta2 = new double[w1.length][w1[0].length];

        //This little diddy is super ugly, and needs explanation. I broke down the formula for the gradient for the hidden layer
        //into its separate components to make it easier for me (and maybe you) to read. The formula used is the one from lab.

        double[] d2_temp_d = new double[hiddenL.length];
        for (i=0;i<hiddenL.length;i++){
            d2_temp_d[i] = Act_Func_Prime(hiddenL[i]);
        }
        double[] d2_temp_c = w2;
        double d2_temp_b = Act_Func_Prime(out);
        double d2_temp_a = 2*(Act_Func(out)-actual);
        double[] d2_temp_e = new double[hiddenL.length];
        for (i = 0;i<hiddenL.length;i++) {
            d2_temp_e[i] = d2_temp_a * d2_temp_b * d2_temp_c[i] * d2_temp_d[i];
        }
        for (i=0;i<w1.length;i++){
            for (j=0;j<ins.length;j++){
                delta2[i][j] = d2_temp_e[i] * ins[j];
            }
        }
        //This concludes the calculation of the gradients, yes it's messy I'm sorry
        //Now to update weights
        for (i=0;i<w1.length;i++){
            w2[i] -= delta3[i] * l_rate;
            for (j=0;j<w1[0].length;j++){
                w1[i][j] -= delta2[i][j] *l_rate;
            }
        }

    }


    double Cost_Calc(double output, double actual){
        return Math.pow(output-actual,2);
    }

    double Output_Calc(double[] weights, double[] ins){
        double a = Summation(weights,ins);
        return a;
    }

    void Hidden_Calc(double[][] weights,double[] ins, double[] hid){
        int i;
        for (i=0;i<hid.length;i++){
            hid[i] = Summation(weights[i],ins);
        }
    }

    double Summation(double[] weights, double[] inputs){
        double result = 0;
        for (int i = 0;i<weights.length;i++){
            result += weights[i]*Act_Func(inputs[i]);
        }
        return result;
    }//This one is called summation but really it functions as the formula for finding the A value for a layer, kind of a counterintuitive name but it gets the job done



    double Act_Func(double x){
        return  1/(1+Math.exp(-x));

    }
    double Act_Func_Prime(double x){
        return Act_Func(x)*(1-Act_Func(x));
    }

    double[][] W1_Init(double[][] weight){
        int i,j;
        for (i=0;i<weight.length;i++){
            for (j=0;j<weight[i].length;j++){
                weight[i][j] = Math.random();
            }
        }
        return weight;
    }
    double[] W2_Init(double[] weight){
        int i;
        for (i=0;i<weight.length;i++){
                weight[i] = Math.random();
        }
        return weight;
    }
    String[] Data_Gen(){
        String[] binaryArr = new String[16];
        int i;

        for (i=0;i<16;i++){
            binaryArr[i] = Integer.toBinaryString(i);
            if (binaryArr[i].length() <4){
                String s = "";
                for (int j = 0;j<(4-binaryArr[i].length());j++){
                    s += "0";
                }
                s+=binaryArr[i];
                binaryArr[i] = s;
            }
        }
        return binaryArr;
    }
    int[] Parity_Gen(String[] in2){
        int i,j,k;
        int[] parity = new int[in2.length];
        for (i=0;i<in2.length;i++){
            k = 0;
            for (j=0;j<in2[i].length();j++){
                if (in2[i].charAt(j) == '1') k++;
            }
            if (k%2 == 1){
                parity[i] = 1;
            }
        }


        return parity;
    }

    public static void main(String[] args) {
        ANN_Parity a = new ANN_Parity();
    }
}