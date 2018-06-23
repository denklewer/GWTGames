package sevendoors.client;

public class CircleTrajectory {
	
	private int _x0;
	private int _radius;
	private int _y0;
	private double _phi0;
	private int _period;
	
	public void calculate(int x1, int y1, int x2, int y2, int period) {
		_radius = (int) Math.sqrt((x1 - x2) * (x1 - x2)
				+ (y1 - y2) * (y1 - y2)) / 2;
		
		_x0 = (x1 + x2) / 2;
		_y0 = (y1 + y2) / 2;
		
		if(_radius == 0)
			return;
		
		double tmp = ((double)(x1 - _x0)) / _radius;
		if (tmp > 1)
			_phi0 = 0;
		else if (tmp < -1)
			_phi0 = Math.PI;
		else 
			_phi0 = Math.acos(tmp);
		if (Math.asin((double) (y1 - _y0) / _radius) < 0) 
			_phi0 = -_phi0;
		_period = period;
	}

	public int getX(int ticks) {
		return _x0
		+ (int) (_radius * Math.cos(_phi0 + Math.PI
				* ((double) ticks / _period)));
	}
	
	public int getY(int ticks) {
		return _y0
		+ (int) (_radius * Math.sin(_phi0 + Math.PI
				* ((double) ticks / _period)));
	}
	
	
}
