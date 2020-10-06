package core.utilities;

import java.util.Arrays;

public class MyByteArrayList {
    byte[] array;
    int currentSize;


    public MyByteArrayList() {
        array = new byte[10];
        currentSize = 0;

    }

    public void add(byte b) {
        if (currentSize < array.length) {
            array[currentSize] = b;
            currentSize++;
        } else {
            int newSize = currentSize * 2;
            byte[] newArray = new byte[newSize];
            for (int i = 0; i < array.length; i++) {
                newArray[i] = array[i];
            }
            array = newArray;
            newArray[currentSize] = b;
            currentSize++;

        }
    }

    public byte get(int index) {
        if (index < 0 || index >= currentSize) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return array[index];
    }

    public int size() {
        return this.currentSize;
    }

    public byte[] getArray() {
        return Arrays.copyOfRange(array, 0, currentSize);
    }


}
