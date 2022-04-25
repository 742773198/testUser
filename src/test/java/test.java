import java.lang.reflect.Array;
import java.util.Arrays;

public class test {
    public static void main(String[] args) {
        int[] array1 =new int[]{1,2};
        int[] array2 =new int[]{3,4};
        System.out.println(new test().findMedianSortedArrays(array1,array2));
    }

    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int m = nums1.length;
        int n = nums2.length;
        int len = m + n;
        int left = -1, right = -1;
        int aStart = 0, bStart = 0;
        for (int i = 0; i <= len / 2; i++) {
            left = right;
            if (aStart < m && (bStart >= n || nums1[aStart] < nums2[bStart])) {
                right = nums1[aStart++];
            } else {
                right = nums2[bStart++];
            }
        }
        if (len%2 == 0)
            return (left + right) / 2.0;
        else
            return right;
    }
}
