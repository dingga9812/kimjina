package c1;

public class ArrayLengthZxample {
	
	public static void main(String[] arge) {

	int[][] intArray = new int[3][4];
	int num=1;
	for(int i=0;i<intArray.length;i++) {

		for(int j=0;j<intArray[i].length;j++) {
			intArray[i][j]=num++;
			System.out.print(intArray[i][j]+"\t");
		}System.out.println();
	}
	
	}
}
