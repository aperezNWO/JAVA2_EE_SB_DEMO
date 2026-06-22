package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;

public class AlgorithmManager {

    public static String generateRandomPoints(int vertexSize, int sampleSize, int sourcePoint) {
        //
        sampleSize = sampleSize - 2; // REMOVER EXTREMOS DE COORDENADAS
        //
        int[][] graph = new int[vertexSize][vertexSize];
        //
        long currentTimeMillis = System.currentTimeMillis();
        Random randX = new Random(currentTimeMillis / 2);
        Random randY = new Random(currentTimeMillis * 2);
        //
        int[] vertexX = fisherYates(sampleSize, randX);
        int[] vertexY = fisherYates(sampleSize, randY);
        //
        List<String> vertexArray = new ArrayList<>();
        //
        for (int index = 0; index < vertexSize; index++) {
            String separator1 = (index < vertexSize - 1) ? "|" : "";
            vertexArray.add(String.format("[%d,%d]%s", vertexX[index], vertexY[index], separator1));
        }
        //
        StringJoiner vertexArrayString = new StringJoiner("");
        for (String vertex : vertexArray) {
            vertexArrayString.add(vertex);
        }
        //
        String separator2    = "■";
        String vertexMatrix  = generateRandomMatrix(vertexArray, graph, vertexSize);
        String vertexList    = dijkstra(vertexArray, graph, vertexSize, sampleSize, sourcePoint);

        //    
        String sortedListEncoded = vertexList.replace(",", "<br/>").replace("\t", "&nbsp;");
        String statusMessage     = String.format("%s%s%s%s%s", vertexArrayString.toString(), separator2, vertexMatrix, separator2, sortedListEncoded);

        //
        return statusMessage;
    }
    //
    public static String generateRandomMatrix(List<String> vertexString, int[][] graph, int vertexSize) {
        //
        StringBuilder statusMessage = new StringBuilder();
        //
        for (int index = 0; index < vertexSize; index++) {
            graph[index][index] = 0;
        }
        //
        long currentTimeMillis = System.currentTimeMillis();
        Random rnd = new Random(currentTimeMillis % 1000);
        //    
        for (int index_x = 0; index_x < vertexSize; index_x++) {
            for (int index_y = (index_x + 1); index_y < vertexSize; index_y++) {
                double randomValue = rnd.nextInt(2);

                if (randomValue == 1) {
                    randomValue = getHipotemuza(vertexString, index_x, index_y);
                }

                graph[index_x][index_y] = (int) randomValue;
                graph[index_y][index_x] = (int) randomValue;
            }
        }
        //
        for (int index_x = 0; index_x < vertexSize; index_x++) {
            int zeroCount = 0;

            for (int index_y = 0; index_y < vertexSize; index_y++) {
                if ((index_x != index_y) && (graph[index_x][index_y] == 0)) {
                    zeroCount++;

                    if (zeroCount == (vertexSize - 1)) {
                        int hipotemuza = (int) getHipotemuza(vertexString, index_x, index_y);
                        graph[index_x][index_y] = hipotemuza;
                        graph[index_y][index_x] = hipotemuza;
                    }
                }
            }
        }
        //
        for (int index_x = 0; index_x < vertexSize; index_x++) {
            //
            String separator_1        = (index_x < vertexSize - 1) ? "|" : "";
            StringBuilder stringArray = new StringBuilder();
            //
            for (int index_y = 0; index_y < vertexSize; index_y++) {
                String separator_2 = (index_y < vertexSize - 1) ? "," : "";
                stringArray.append(String.format("%d%s", graph[index_x][index_y], separator_2));
            }
            //
            String stringArrayValues = String.format("{%s}", stringArray.toString());
            statusMessage.append(String.format("%s%s", stringArrayValues, separator_1));
        }
        //
        return statusMessage.toString();
    }
    //
    private static double getHipotemuza(List<String> vertexString, int index_x, int index_y) 
    {
        //
        String[] coord_source = vertexString.get(index_y).replaceAll("[|\\[\\]]", "").split(",");
        String[] coord_dest   = vertexString.get(index_x).replaceAll("[|\\[\\]]", "").split(",");
        //
        double coord_source_x = Double.parseDouble(coord_source[0]);
        double coord_source_y = Double.parseDouble(coord_source[1]);
        double coord_dest_x   = Double.parseDouble(coord_dest[0]);
        double coord_dest_y   = Double.parseDouble(coord_dest[1]);
        double coord_x        = Math.abs(coord_dest_x - coord_source_x);
        double coord_y        = Math.abs(coord_dest_y - coord_source_y);
        //
        double hipotemuza     = pythagorean(coord_x, coord_y);
        //
        return hipotemuza;
    }
    //
    private static double pythagorean(double coord_x, double coord_y) {
        //
        double power       = 2;
        double pythagorean = Math.sqrt(
            Math.pow(coord_x, power) +
            Math.pow(coord_y, power)
        );
        //
        return pythagorean;
    }
    //
    public static int[] fisherYates(int count, Random rand) {
        //
        int[] deck = new int[count];
        //    
        for (byte i = 0; i < count; i++)
            deck[i] = (i + 1);
        //
        for (byte i = 0; i <= count - 2; i++) {
            int j = rand.nextInt(count - i);
            if (j > 0) {
                int curVal = deck[i];
                deck[i] = deck[i + j];
                deck[i + j] = curVal;
            }
        }
        //
        for (int i = count - 1; i >= 1; i--) {
            int j = rand.nextInt(i + 1);
            if (j != i) {
                int curVal = deck[i];
                deck[i] = deck[j];
                deck[j] = curVal;
            }
        }
        //
        return deck;
    }
    //
    public static String dijkstra(List<String> vertex, int[][] graph, int vertexSize, int sampleSize, int sourcePoint) {
            // Driver Code 
            GFG t = new GFG();
            t.dijkstra(graph, sourcePoint, vertexSize);
    
            String integerFormat = "%02d";
            StringBuilder status = new StringBuilder();
            
            for (int index = 0; index < t.dist.length; index++) {
                // Correct values
                if (t.dist[index] >= Integer.MAX_VALUE) {
                    t.dist[index] = 0;
                }
    
                String separator = (index < (t.dist.length - 1)) ? "," : "";
                //1. x 01
                //2. x <[6;8]>
                //3. x -41
                //4. x -[0;3]≡[3;7]≡[7;6]≡[6;2]≡[2;1]≡
                //5. x <br/>
                status.append(String.format("%s<%s>-%s-%s%s",
                        //[OK] 1. x 01
                        String.format(integerFormat, index),
                        //[OK] 2. x <[6;8]>
                        vertex.get(index).replace(",", ";").replace("|", ""),
                        //[OK] 3. x -41
                        String.format(integerFormat, t.dist[index]),
                        //[OK] 4. x -[0;3]≡[3;7]≡[7;6]≡[6;2]≡[2;1]≡
                        t.path.get(index).replace(",", ";"),
                        //[OK] 5. x <br/>
                        separator
                ));

                /*  CORRECT0
                   00<[5;19]>-00-<br/>
                   01<[6;8]>-41-[0;3]≡[3;7]≡[7;6]≡[6;2]≡[2;1]≡<br/>
                   02<[10;9]>-37-[0;3]≡[3;7]≡[7;6]≡[6;2]≡<br/>
                   03<[11;14]>-07-[0;3]≡<br/>
                   04<[4;12]>-07-[0;4]≡<br/>
                   05<[16;6]>-16-[0;3]≡[3;5]≡<br/>
                   06<[18;20]>-24-[0;3]≡[3;7]≡[7;6]≡<br/>
                   07<[19;11]>-15-[0;3]≡[3;7]≡<br/>
                   08<[20;1]>-43-[0;3]≡[3;7]≡[7;6]≡[6;8]≡
                */

     
            }
            //
            return status.toString();
    }

}
