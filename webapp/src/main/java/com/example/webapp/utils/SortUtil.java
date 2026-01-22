package com.example.webapp.utils;

public class SortUtil {

    /**
     * 选择排序:常用于取序列中最大最小的几个数时。
     *  1。遍历整个序列，将最小的数放在最前面。
     *  2。遍历剩下的序列，将最小的数放在最前面。
     *  3。 重复第二步，直到只剩下一个数。
     *  如何写成代码：
     *    1.首先确定循环次数，并且记住当前数字和当前位置。
     *    2.将当前位置后面所有的数与当前数字进行对比，小数赋值给key，并记住小数的位置。
     *    3.比对完成后，将最小的值与第一个数的值交换。
     *    4.重复2、3步。
     * @param a
     */
    public static void selectSort(int[] a) {
        int length = a.length;
        //循环次数
        for (int i = 0; i < length; i++) {
            int key = a[i];
            int position=i;
            //选出最小的值和位置
            for (int j = i + 1; j < length; j++) {
                if (a[j] < key) {
                    key = a[j];
                    position = j;
                }
            }
            //交换位置
            a[position]=a[i];
            a[i]=key;
        }
    }

    /**
     * 插入排序:把新的数据插入到已经排好的数据列中。
     *  1。将第一个数和第二个数排序，然后构成一个有序序列
     *  2。将第三个数插入进去，构成一个新的有序序列。
     *  3。对第四个数、第五个数……直到最后一个数，重复第二步。
     *  如何写写成代码：
     *    1.首先设定插入次数，即循环次数，for(int i=1;i<length;i++)，1个数的那次不用插入。
     *    2.设定插入数和得到已经排好序列的最后一个数的位数。insertNum和j=i-1。
     *    3.从最后一个数开始向前循环，如果插入数小于当前数，就将当前数向后移动一位。
     *    4.将当前数放置到空着的位置，即j+1。
     * @param a
     */
    public static void insertSort(int[] a){
        //数组长度，将这个提取出来是为了提高速度。
        int length=a.length;
        //要插入的数
        int insertNum;
        //插入的次数
        for(int i=1;i<length;i++){
            //要插入的数
            insertNum=a[i];
            //已经排序好的序列元素个数
            int j=i-1;
            //序列从后到前循环，将大于insertNum的数向后移动一格
            while(j>=0&&a[j]>insertNum){
                //元素移动一格
                a[j+1]=a[j];
                j--;
            }
            //将需要插入的数放在要插入的位置。
            a[j+1]=insertNum;
        }
    }


    /**
     * 冒泡排序
     *  1。将序列中所有元素两两比较，将最大的放在最后面。
     *  2。将剩余序列中所有元素两两比较，将最大的放在最后面。
     *  3。重复第二步，直到只剩下一个数。
     *  如何写成代码：
     *    1.设置循环次数。
     *    2.设置开始比较的位数，和结束的位数。
     *    3.两两比较，将最小的放到前面去。
     *    4.重复2、3步，直到循环次数完毕。
     * @param a
     */
    public static void bubbleSort(int[] a){
        int temp;
        for(int i=0;i<a.length;i++){
            for(int j=0;j<a.length-i-1;j++){
                if(a[j]>a[j+1]){
                    temp=a[j];
                    a[j]=a[j+1];
                    a[j+1]=temp;
                }
            }
        }
    }

    /**
     * 快速排序
     *    1。选择第一个数为p，小于p的数放在左边，大于p的数放在右边。
     *    2。递归的将p左边和右边的数都按照第一步进行，直到不能递归。
     * @param numbers
     * @param start
     * @param end
     * 执行：quickSort(arr,0,arr.length-1);
     */
    public static void quickSort(int[] numbers, int start, int end) {
        if (start < end) {
            // 选定的基准值（第一个数值作为基准值）
            int base = numbers[start];
            // 记录临时中间值
            int temp;
            int i = start, j = end;
            do {
                while ((numbers[i] < base) && (i < end)){
                    i++;
                }
                while ((numbers[j] > base) && (j > start)){
                    j--;
                }
                if (i <= j) {
                    temp = numbers[i];
                    numbers[i] = numbers[j];
                    numbers[j] = temp;
                    i++;
                    j--;
                }
            } while (i <= j);
            if (start < j){
                quickSort(numbers, start, j);
            }
            if (end > i){
                quickSort(numbers, i, end);
            }
        }
    }

    public static void main(String[] args) {
        int[] arr = {8,2,1,4,3,99,122,44};
        quickSort(arr,0,arr.length-1);
        for (int i : arr) {
            System.out.println(i);
        }
    }
}
