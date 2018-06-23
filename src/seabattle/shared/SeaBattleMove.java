package seabattle.shared;

public class SeaBattleMove {

		public int _x;
		public int _y;
		private byte _side;
		private int _result;
		
		public SeaBattleMove() {
		}
		
		public SeaBattleMove(int x, int y, byte side){
			_x = x;
			_y = y;
			_side = side;
		}
		
		public SeaBattleMove(int x, int y, byte side, int result){
			_x = x;
			_y = y;
			_side = side;
			_result = result;
		}

		public String toString() {
			return "[" + _x + ", " + _y + "]" + " side = " + _side;
		}

		public int getResult() {
			return _result;
		}
		
		public byte getSide() {
			return _side;
		}

		public void setResult(int result) {
			_result = result;
		}
}