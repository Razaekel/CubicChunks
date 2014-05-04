package cuchaz.cubicChunks.gen.procedural;

public class Interpolators {

	/**
	 * Linear Interpolation
	 * 
	 * @param a minimum point
	 * @param b maximum point
	 * @param x location between a and b to get interpolated value at
	 * @return interpolated value
	 */
	public double lerp(double a, double b, double x)
	{
		return a + x * (b - a);
	}
	
	/**
	 * Cosine Interpolation
	 * 
	 * @param a minimum point
	 * @param b maximum point
	 * @param x location between a and b to get interpolated value at
	 * @return interpolated value
	 */
	public double cserp(double a, double b, double x)
	{
		double c = x * 3.14159265;
		double c1 = (1 - Math.cos(c)) * .5;
		
		return a*(1-c1) + b*c1;
	}
}