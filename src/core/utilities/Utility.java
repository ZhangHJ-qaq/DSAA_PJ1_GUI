package core.utilities;

import java.io.File;

public class Utility {
    /**
     * 将一个byte类型的数据转换成八位字符串
     *
     * @param b byte类型数据
     * @return 对应的八位字符串
     */
    public static String to8DigitBinaryString(byte b) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Integer.toBinaryString(b));
        if (stringBuilder.length() == 8) {
            return stringBuilder.toString();
        } else if (stringBuilder.length() < 8) {
            while (stringBuilder.length() < 8) {
                stringBuilder.insert(0, 0);
            }
            return stringBuilder.toString();
        } else {
            stringBuilder.delete(0, 24);
            return stringBuilder.toString();
        }
    }

    /**
     * 给出一个文件或文件夹，返回其字节数
     *
     * @param file 文件或文件夹
     * @return 字节数
     */
    public static long getSize(File file) {
        if (!file.exists()) {
            return 0;
        }

        if (file.isFile()) {
            return file.length();
        }

        File[] files = file.listFiles();

        long totalSize = 0;

        for (File singleFile : files
        ) {
            totalSize += getSize(singleFile);
        }

        return totalSize;

    }


}
