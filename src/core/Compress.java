package core;

import core.utilities.CastArrayUtil;
import core.utilities.Utility;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.util.*;

public class Compress {
    private static String rootFolder;
    private static ArrayList<String> dirList;
    private static ArrayList<HuffmanSingleFileData> fileList;


    /**
     * 根据给出的文件目录，输出其压缩后对应的HuffmanSingleFileData
     *
     * @param dir 文件目录
     * @return 压缩后对应的HuffmanSingleFileData
     */
    private static HuffmanSingleFileData getCompressedDataOfSingleFile(String dir, String rootPath) throws IOException {

        //构造输入和输出文件
        File srcFile = new File(dir);

        //构造输入流
        DataInputStream in = new DataInputStream(
                new BufferedInputStream(new FileInputStream(srcFile)));


        byte[] srcFileBytes = new byte[(int) srcFile.length()];

        //读入源文件的字节流
        in.read(srcFileBytes);

        in.close();

        //构造用于封装压缩后文件信息的对象
        HuffmanSingleFileData huffmanSingleFileData = new HuffmanSingleFileData();

        //由源文件的字节数组构造哈夫曼树
        HuffmanTree myTree = HuffmanTree.generateHuffmanTree(srcFileBytes);

        huffmanSingleFileData.huffmanTree = myTree;
        huffmanSingleFileData.originalFilename = srcFile.getName();
        huffmanSingleFileData.originalFileSize = srcFile.length();

        //将绝对路径转换成相对路径存储
        if (rootPath != null) {
            huffmanSingleFileData.originalRelevantPath = dir.replace(rootPath, "");
        }

        //获取压缩后的字节数组
        byte[] compressedBytes = myTree.encode(srcFileBytes);

        huffmanSingleFileData.compressedBytes = compressedBytes;
        huffmanSingleFileData.numOfZerosAdded = myTree.getNumberOfZerosAdded();

        return huffmanSingleFileData;

    }

    /**
     * 将单个文件的数据转换回byte数组
     *
     * @param huffmanSingleFileData 单个文件的数据
     * @return byte数组
     */
    private static byte[] decompressDataOfSingleFile(HuffmanSingleFileData huffmanSingleFileData) {


        //得到字符和二进制字符串对应的哈希表
        HuffmanTree huffmanTree = huffmanSingleFileData.huffmanTree;

        //在哈夫曼树中对其进行解码
        byte[] recoveredBytes = huffmanTree.decode(huffmanSingleFileData.compressedBytes,
                huffmanSingleFileData.originalFileSize);

        return recoveredBytes;
    }


    /**
     * 压缩的主函数
     *
     * @param src 源路径
     * @param des 目标路径
     * @throws IOException 运行过程中可能抛出的异常
     */
    public static CompressResult compress(String src, String des) throws IOException {
        File srcFile = new File(src);
        File desFile = new File(des);


        //先检查输入的路径是否存在
        if (!srcFile.exists()) {
            throw new IOException("The source is nonexistent!");
        }

        //规定：压缩时输出的路径必须是一个文件。
        if (desFile.isDirectory()) {
            throw new IOException("The destination must be a file, not a directory");
        }

        CompressResult compressResult = new CompressResult();

        //得到源文件的字节数
        compressResult.originalFileSize = Utility.getSize(srcFile);

        long startTime = System.currentTimeMillis();

        //如果输入的源路径指向的是一个单独的文件的话
        if (srcFile.isFile()) {
            Compress.compressSingleFile(srcFile, desFile);

        } else {//反之如果输入的源路径指向的是一个文件夹的话
            Compress.compressAFolder(srcFile, desFile);
        }

        long endTime = System.currentTimeMillis();

        //得到消耗的时间和压缩速率
        compressResult.timeConsumed = (endTime - startTime) / 1000.0;
        compressResult.speed = compressResult.originalFileSize / compressResult.timeConsumed;

        //得到压缩后文件的字节数
        compressResult.compressedFileSize = Utility.getSize(desFile);

        return compressResult;

    }

    private static void compressSingleFile(File srcFile, File desFile) throws IOException {

        //构造输入输出
        ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(desFile)));

        //建立压缩数据
        HuffmanZipData huffmanZipData = new HuffmanZipData();

        //将数据设置为单一文件
        huffmanZipData.type = HuffmanZipData.TYPE_SINGLE_FILE;

        HuffmanSingleFileData huffmanSingleFileData = Compress.getCompressedDataOfSingleFile(srcFile.getPath(), null);

        huffmanZipData.fileList.add(huffmanSingleFileData);
        huffmanZipData.originalSize = Utility.getSize(srcFile);

        huffmanSingleFileData.originalFileSize = Utility.getSize(srcFile);

        out.writeObject(huffmanZipData);

        out.close();


    }

    private static void compressAFolder(File srcFile, File desFile) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(desFile))
        );

        //初始化压缩后的数据变量，将其类型设定为目录
        HuffmanZipData huffmanZipData = new HuffmanZipData();
        huffmanZipData.type = HuffmanZipData.TYPE_DIRECTORY;
        huffmanZipData.originalSize = Utility.getSize(srcFile);

        //初始化三个静态变量
        rootFolder = srcFile.getParentFile().getPath();
        dirList = new ArrayList<>();
        fileList = new ArrayList<>();

        //在目录列表中加入根目录
        dirList.add(srcFile.getPath().replace(rootFolder, ""));

        //递归遍历目录，压缩文件，更新文件列表，更新目录列表
        traverse(srcFile);

        huffmanZipData.fileList = fileList;
        huffmanZipData.dirList = dirList;

        out.writeObject(huffmanZipData);
        out.close();

    }

    /**
     * 通过递归，遍历dir下的全部文件和目录。将目录信息存放在静态变量dirList中，将压缩后的文件信息存放在静态变量fileList中
     *
     * @param dir 目录
     */
    private static void traverse(File dir) throws IOException {

        File[] childrenFiles = dir.listFiles();

        for (File childFile : childrenFiles
        ) {
            if (childFile.isDirectory()) {//如果子文件是目录
                //在目录列表中加入目录
                dirList.add(childFile.getPath().replace(rootFolder, ""));

                //递归地遍历
                traverse(childFile);
            } else if (childFile.isFile()) {//如果子文件是文件

                //获取输入流
                DataInputStream in = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(childFile))
                );

                //读取文件的数据
                byte[] originalBytes = new byte[(int) childFile.length()];
                in.read(originalBytes);

                in.close();

                //得到单个文件的数据
                HuffmanSingleFileData huffmanSingleFileData = getCompressedDataOfSingleFile(childFile.getPath(), rootFolder);


                fileList.add(huffmanSingleFileData);

            }
        }


    }


    /**
     * 解压的主函数
     *
     * @param src 已压缩文件的源路径
     * @param des 解压的目标路径
     * @throws IOException 运行中可能抛出的IOException异常
     */
    public static CompressResult decompress(String src, String des) throws IOException {
        File srcFile = new File(src);
        File desFile = new File(des);

        //判断源目录和目标目录是否存在
        if (!srcFile.exists() || !desFile.exists()) {
            throw new IOException("The source or destination is nonexistent!");
        }

        //判断源目录是否是一个文件
        if (!srcFile.isFile()) {
            throw new IOException("The source must be a file!");
        }

        //判断目标目录是否是一个目录
        if (!desFile.isDirectory()) {
            throw new IOException("The destination must be a directory");
        }

        CompressResult compressResult = new CompressResult();

        long startTime = System.currentTimeMillis();

        ObjectInputStream in = null;
        //获得输入流
        try {
            in = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(srcFile))
            );
        } catch (Exception e) {
            throw new IOException("The file format is not supported.");

        }


        //从输入流中读取数据，处理异常
        HuffmanZipData huffmanZipData;
        try {
            huffmanZipData = (HuffmanZipData) in.readObject();
        } catch (Exception e) {
            throw new IOException("The file format is not supported.");
        }

        long originalSize;

        //如果数据中显示压缩的是单一的文件
        if (huffmanZipData.type.equals(HuffmanZipData.TYPE_SINGLE_FILE)) {
            originalSize = decompressASingleFile(huffmanZipData, desFile);

        } else {//否则如果压缩的是一个文件夹
            originalSize = decompressFolder(huffmanZipData, desFile);
        }

        long endTime = System.currentTimeMillis();


        compressResult.originalFileSize = originalSize;


        compressResult.timeConsumed = (endTime - startTime) / 1000.0;

        compressResult.speed = compressResult.originalFileSize / compressResult.timeConsumed;

        return compressResult;

    }

    /**
     * 解压单一的文件
     *
     * @param huffmanZipData 文件数据
     * @param desDir         目标的目录
     * @return 源文件的MB数
     */
    private static long decompressASingleFile(HuffmanZipData huffmanZipData, File desDir) throws IOException {

        //根据desDir中文件的目录及文件数据中存储的原有的文件名，获得真正的解压文件的信息
        File ultimateDesFile =
                new File(desDir.getPath() + "/" + huffmanZipData.fileList.get(0).originalFilename);

        HuffmanSingleFileData huffmanSingleFileData = huffmanZipData.fileList.get(0);

        //从哈夫曼树中恢复数据
        byte[] restoredBytes = huffmanSingleFileData.huffmanTree.decode(
                huffmanSingleFileData.compressedBytes, huffmanSingleFileData.originalFileSize
        );

        //获得输出流
        DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(ultimateDesFile))
        );

        //输出
        out.write(restoredBytes);

        out.close();

        return huffmanZipData.originalSize;

    }

    private static long decompressFolder(HuffmanZipData huffmanZipData, File desFile) throws IOException {
        ArrayList<String> dirList = huffmanZipData.dirList;

        //先根据压缩信息内的内容，创建文件夹
        for (String relativeDir : dirList
        ) {
            File file = new File(desFile.getPath() + relativeDir);
            file.mkdir();
        }

        ArrayList<HuffmanSingleFileData> huffmanSingleFileDataArrayList = huffmanZipData.fileList;

        for (HuffmanSingleFileData singleFileData : huffmanSingleFileDataArrayList
        ) {
            //获得文件
            File file = new File(desFile.getPath() + singleFileData.originalRelevantPath);

            //获得输出流
            BufferedOutputStream out = new BufferedOutputStream(
                    new FileOutputStream(file)
            );

            out.write(decompressDataOfSingleFile(singleFileData));

            out.close();

        }

        return huffmanZipData.originalSize;

    }

}
