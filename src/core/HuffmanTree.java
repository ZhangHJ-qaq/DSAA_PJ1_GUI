package core;

import core.utilities.MyByteArrayList;
import core.utilities.Utility;

import java.io.Serializable;
import java.util.*;

public class HuffmanTree implements Serializable {
    private HuffmanNode root;
    private HashMap<Byte, String> encodingTable;
    private int numberOfZerosAdded;

    public int getNumberOfZerosAdded() {
        return numberOfZerosAdded;
    }

    public HuffmanTree(HuffmanNode root) {
        this.root = root;
        this.encodingTable = new HashMap<>();
    }

    /**
     * 根据传入的字节数组，统计每个字节的出现频率
     *
     * @param byteArray 字节数组
     * @return 存放了字节和其对应频率的Hashmap
     */
    private static int[] countFrequency(byte[] byteArray) {

        int[] frequencyArray = new int[256];

        //遍历字符数组
        for (byte b : byteArray) {
            //Java中没有unsigned byte这种数据类型，只有byte，其范围是-128~127，而数组的下标范围是0~255
            //在读取到的byte数据上增加128，以完成从byte范围到数组下标范围的映射
            frequencyArray[b + 128]++;

        }

        return frequencyArray;

    }

    /**
     * 根据存储了字节和频率的hashmap，返回node的队列
     *
     * @param frequencyArray 存储了字节频率的数组
     * @return 包含node的队列
     */
    private static PriorityQueue<HuffmanNode> generateNodeQueue(int[] frequencyArray) {
        PriorityQueue<HuffmanNode> huffmanNodePriorityQueue = new PriorityQueue<>();

        for (int i = 0; i < frequencyArray.length; i++) {
            if (frequencyArray[i] <= 0) continue;

            //数组下标 减 128 =byte数据
            //数组值=频率
            HuffmanNode huffmanNode = new HuffmanNode((byte) (i - 128), frequencyArray[i], null, null);

            huffmanNodePriorityQueue.add(huffmanNode);
        }


        return huffmanNodePriorityQueue;

    }

    /**
     * 根据哈夫曼节点的优先队列，生成哈夫曼树
     *
     * @param huffmanNodePriorityQueue 哈夫曼节点的优先队列
     * @return 哈夫曼树的根节点
     */
    private static HuffmanTree generateHuffmanTreeWithPriorityQueue(PriorityQueue<HuffmanNode> huffmanNodePriorityQueue) {

        //创建结果节点

        while (huffmanNodePriorityQueue.size() >= 2) {
            HuffmanNode newNode = new HuffmanNode();

            //从优先队列中拉出权重最低的两个元素
            HuffmanNode node1 = huffmanNodePriorityQueue.poll();
            HuffmanNode node2 = huffmanNodePriorityQueue.poll();

            //将拉出的两个节点成为新建节点的子节点
            newNode.left = node1;
            newNode.right = node2;
            newNode.weight = node1.weight + node2.weight;

            //将新建的节点加入到优先队列中
            huffmanNodePriorityQueue.add(newNode);
        }

        return new HuffmanTree(huffmanNodePriorityQueue.poll());
    }

    public static HuffmanTree generateHuffmanTree(byte[] bytes) {
        int[] frequencyArray = countFrequency(bytes);
        PriorityQueue<HuffmanNode> huffmanNodePriorityQueue = generateNodeQueue(frequencyArray);
        return generateHuffmanTreeWithPriorityQueue(huffmanNodePriorityQueue);

    }

    /**
     * 根据哈夫曼树，得到哈夫曼编码表
     *
     * @return HashMap<Byte, String>
     */
    public HashMap<Byte, String> getEncodingTable() {
        //如果传入的是空文件
        if (this.root == null) {
            this.encodingTable = null;
            return this.encodingTable;
        }
        this.computeEncodingTable(root, new StringBuilder());
        return this.encodingTable;
    }

    private void computeEncodingTable(HuffmanNode node, StringBuilder stringBuilder) {

        if (node.isLeafNode()) {
            this.encodingTable.put(node.data, stringBuilder.toString());
            return;
        }
        if (node.left != null) {
            computeEncodingTable(node.left, stringBuilder.append(0));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        if (node.right != null) {
            computeEncodingTable(node.right, stringBuilder.append(1));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

    }

    public byte[] encode(byte[] srcFileBytes) {

        if (this.encodingTable == null || this.encodingTable.size() == 0) {
            this.getEncodingTable();
        }

        if (srcFileBytes.length == 0) {
            return new byte[0];
        }

        StringBuilder stringBuilder = new StringBuilder();

        MyByteArrayList byteList = new MyByteArrayList();

        //遍历源文件的字节数组
        for (int i = 0; i < srcFileBytes.length; i++) {
            //先在stringBuilder中加入这个字节对应的编码


            stringBuilder.append(this.encodingTable.get(srcFileBytes[i]));

            //如果stringBuilder的字节数超过了8，则取出前八位写入list中
            while (stringBuilder.length() >= 8) {
                byte byteeeee = (byte) Integer.parseInt(stringBuilder.substring(0, 8), 2);
                byteList.add(byteeeee);
                stringBuilder.delete(0, 8);
            }
        }

        int count = 0;
        //最后不满8位的话用0凑
        while (stringBuilder.length() < 8) {
            stringBuilder.append(0);
            count++;
        }
        this.numberOfZerosAdded = count;
        byteList.add((byte) Integer.parseInt(stringBuilder.substring(0, 8), 2));


        //将arraylist中的内容转换到byte数组
        byte[] compressedBytes =byteList.getArray();


        return compressedBytes;

    }

    public byte[] decode(byte[] compressedBytes, long originalFileSize) {

        if (this.encodingTable == null || this.encodingTable.size() == 0) {
            this.getEncodingTable();
        }

        if (compressedBytes.length == 0) {
            return new byte[0];
        }

        //反转编码哈希表
        HashMap<String, Byte> reversedMap = new HashMap<>();
        Set<Map.Entry<Byte, String>> entrySet = encodingTable.entrySet();
        for (Map.Entry<Byte, String> entry : entrySet
        ) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }

        //根据原来文件的大小，创建恢复后的字节数组
        byte[] restoredBytes = new byte[(int) originalFileSize];

        StringBuilder stringBuilder = new StringBuilder();

        int i = 0;
        int p = 0;
        while (i < restoredBytes.length) {

            //每次往stringbuilder中读至多32个字节
            for (int x = 0; x < 32 && p < compressedBytes.length; x++, p++) {
                stringBuilder.append(Utility.to8DigitBinaryString(compressedBytes[p]));

            }

            //两个指针
            int startPtr = 0;
            int endPtr = 0;


            //试探性
            while (endPtr < stringBuilder.length() + 1 && i < restoredBytes.length) {
                if (reversedMap.containsKey(stringBuilder.substring(startPtr, endPtr))) {
                    restoredBytes[i] = reversedMap.get(stringBuilder.substring(startPtr, endPtr));
                    startPtr = endPtr;
                    i++;
                }
                endPtr++;
            }

            stringBuilder.delete(0, startPtr);


        }


        return restoredBytes;
    }

}
