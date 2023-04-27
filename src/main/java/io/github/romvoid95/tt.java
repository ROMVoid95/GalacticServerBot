package io.github.romvoid95;

public class tt {
	public static void main(String[] args) {
		int[] a = { 4572400, 2680300, 7774700, 11873200, 12934900, 19010800, 16202800, 1362000, 14313700, 8418000,
				8418000, 1362000, 16202800, 14313700, 2483600, 1766600, 1362000, 7774700, 12934900, 2680300, 16202800,
				7774700, 7774700, 8418000, 12934900, 16202800 };
		
		System.out.println( sum(a));
	}
    static int sum(int[] arr){
        int sum = 0;
        for (int a = 0; a < arr.length; a++){
            sum = sum + arr[a];
        }
        return sum;
    }
}
