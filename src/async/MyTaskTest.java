package async;

import java.util.Arrays;

public class MyTaskTest {

	public static void main(String[] args) throws Exception {
		new MyTaskTest().test();
	}

	private void test() throws Exception {

		final int[] myarray = new int[] { 4, 5, 8, 1, 5, 4, 6, 25, 2, 58, 8 };

		Integer num = new TaskWithTimeout<Integer>(5000) {

			@Override
			public Integer perform() {
				Arrays.sort(myarray);
				return myarray[myarray.length - 1];
			}

		}.performWithTimeout();

		System.out.println(num);
	}
}
