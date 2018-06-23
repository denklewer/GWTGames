package games.client.sprites;

import java.util.List;

public class Rectangle {

	public int x;
	public int y;
	public int width;
	public int height;

	public Rectangle(final int x, final int y, final int w, final int h) {
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}

	public Rectangle join(final Rectangle r) {
		final int x1 = Math.min(x, r.x);
		final int x2 = Math.max(x + width, r.x + r.width);
		final int y1 = Math.min(y, r.y);
		final int y2 = Math.max(y + height, r.y + r.height);
		return new Rectangle(x1, y1, x2 - x1, y2 - y1);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getSquare() {
		return width * height;
	}

	public boolean intersects(final Rectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		final int tx = this.x;
		final int ty = this.y;
		final int rx = r.x;
		final int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		// overflow || intersect
		return (rw < rx || rw > tx) && (rh < ry || rh > ty)
				&& (tw < tx || tw > rx) && (th < ty || th > ry);
	}

	public Rectangle intersection(final Rectangle r) {
		int tx1 = this.x;
		int ty1 = this.y;
		final int rx1 = r.x;
		final int ry1 = r.y;
		long tx2 = tx1;
		tx2 += this.width;
		long ty2 = ty1;
		ty2 += this.height;
		long rx2 = rx1;
		rx2 += r.width;
		long ry2 = ry1;
		ry2 += r.height;
		if (tx1 < rx1) {
			tx1 = rx1;
		}
		if (ty1 < ry1) {
			ty1 = ry1;
		}
		if (tx2 > rx2) {
			tx2 = rx2;
		}
		if (ty2 > ry2) {
			ty2 = ry2;
		}
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		if (tx2 < Integer.MIN_VALUE) {
			tx2 = Integer.MIN_VALUE;
		}
		if (ty2 < Integer.MIN_VALUE) {
			ty2 = Integer.MIN_VALUE;
		}
		return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
	}

	@Override
	public String toString() {
		return "( " + x + ", " + y + ", " + width + ", " + height + ")";
	}

	Rectangle findIntersected(final List<Rectangle> dirtyRegions) {
		for (final Rectangle rectangle : dirtyRegions) {
			if (rectangle.intersects(this)) {
				return rectangle;
			}
			final Rectangle union = rectangle.join(this);
			if (union.getSquare() == getSquare() + rectangle.getSquare()) {
				return rectangle;
			}
		}
		return null;
	}

	public boolean isEmpty() {
		return width <= 0 || height <= 0;
	}
	
	public void setBounds(final int x, final int y, final int width, final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean contains(final int x, final int y) {
		return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
	}

	public void translate(final int i, final int j) {
		x += i;
		y += j;
	}


}
