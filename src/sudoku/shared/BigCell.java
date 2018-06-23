package sudoku.shared;

import games.shared.Point;

import java.util.Vector;

public class BigCell {
	public static int ROW = 1;

	public static int COL = 2;

	public static int BIGCELL = 3;

	private int _style;

	private Point _XY;

	public Cell[] _cells = new Cell[9];

	private SudokuModel _sudoku;

	public BigCell(final Point point, final Cell[] cells, final int style, final SudokuModel sudoku) {
		this._style = style;
		this._cells = cells;
		this._XY = point;
		this._sudoku = sudoku;
	}

	public void setCell(final int number, final Cell newCell) {
		_cells[number] = newCell;
	}

	public boolean solve_HiddenSingle(final int currnumber) {
		// esli v stroke ili v stolbce est tolko odno mesto kuda mogno postavit
		// tsifru
		if (getLittleNumbers(currnumber).size() == 1) {
			for (int i = 0; i < 9; i++) {
				if (_cells[i].isStyleNone()
						&& _cells[i].getLittleNumber(currnumber)) {
					_cells[i].setNumber(currnumber);
					_cells[i].setStyle(Cell.SOLVED);
//					System.out.println(_sudoku);
//					System.out.println(_sudoku.getTable());
//					System.out.println(_cells[i]);

					_sudoku.getTable().tableChanged(_cells[i].getCol(),
							_cells[i].getRow());
//					if (_sudoku.isPrintlog()) {
//						System.out.println("new_solve_found: " + currnumber
//								+ " " + _cells[i].getCol() + "_"
//								+ _cells[i].getRow());
//					}
					_sudoku.increaseComplication(
							SolveLogic.COMPLICATION_2_HIDDENSINGLE, currnumber,
							_cells[i].getCol(), _cells[i].getRow());
					return true;
				}
			}
		}
		return false;
	}

	public boolean Solve_NakedPairs() {
		boolean solved = false;
		for (int firstnumber = 1; firstnumber <= 9; firstnumber++) {
			for (int secondnumber = firstnumber + 1; secondnumber <= 9; secondnumber++) {
				if (getLittleNumbersCount(firstnumber) == 2
						&& getLittleNumbersCount(secondnumber) == 2) {
					final Vector<Integer> firstVector = getLittleNumbers(firstnumber);
					final Vector<Integer> secondVector = getLittleNumbers(secondnumber);
					if (firstVector.get(0) == secondVector.get(0)
							&& firstVector.get(1) == secondVector.get(1)) {
						final Vector<Integer> tmpcannumbers = new Vector<Integer>();
						tmpcannumbers.add(firstnumber);
						tmpcannumbers.add(secondnumber);
						for (int i = 0; i < 9; i++) {
							if (i == firstVector.get(0)
									|| i == firstVector.get(1)) {
								if (!_cells[i].isCanBeNumberOnly(tmpcannumbers)) {
									_cells[i]
											.setLittleNumbersOnly(tmpcannumbers);
									solved = true;
//									if (SudokuModel.printLog) {
//										System.out
//												.println("new_cannotnumber_found: "
//														+ firstnumber
//														+ "&"
//														+ secondnumber
//														+ ", "
//														+ _cells[firstVector
//																.get(0)]
//																.getCol()
//														+ "_"
//														+ _cells[firstVector
//																.get(1)]
//																.getRow());
//									}
									_sudoku
											.increaseComplication(
													SolveLogic.COMPLICATION_5_NAKEDPAIRS,
													firstnumber, _cells[i]
															.getCol(),
													_cells[i].getRow());
									_sudoku
											.increaseComplication(
													SolveLogic.COMPLICATION_5_NAKEDPAIRS,
													secondnumber, _cells[i]
															.getCol(),
													_cells[i].getRow());

								}
							} else {
								if (_cells[i].isCanBeNumber(tmpcannumbers)) {
									_cells[i]
											.setLittleNumbersNot(tmpcannumbers);
									solved = true;
//									if (SudokuModel.printLog) {
//										System.out
//												.println("new_cannotnumber_found: "
//														+ firstnumber
//														+ "&"
//														+ secondnumber
//														+ ", "
//														+ _cells[firstVector
//																.get(0)]
//																.getCol()
//														+ "_"
//														+ _cells[firstVector
//																.get(1)]
//																.getRow());
//									}
									_sudoku
											.increaseComplication(
													SolveLogic.COMPLICATION_5_NAKEDPAIRS,
													firstnumber, _cells[i]
															.getCol(),
													_cells[i].getRow());
									_sudoku
											.increaseComplication(
													SolveLogic.COMPLICATION_5_NAKEDPAIRS,
													secondnumber, _cells[i]
															.getCol(),
													_cells[i].getRow());
								}
							}
						}
						if (solved) {
							return true;
						}
					}
				}
			}
		}

		return solved;
	}

	public boolean solve_NakedTriples() {
		boolean solved = false;
		for (int firstcell = 0; firstcell < 9; firstcell++) {
			if (_cells[firstcell].getStyle() == Cell.NONE) {
				for (int secondcell = firstcell + 1; secondcell < 9; secondcell++) {
					if (_cells[secondcell].getStyle() == Cell.NONE) {
						for (int thirdcell = secondcell + 1; thirdcell < 9; thirdcell++) {
							if (_cells[thirdcell].getStyle() == Cell.NONE) {
								final boolean[] variousnumbers = new boolean[9];
								for (int i = 0; i < 9; i++) {
									variousnumbers[i] = false;
								}
								for (int i = 1; i <= 9; i++) {
									if (_cells[firstcell].getLittleNumber(i)) {
										variousnumbers[i - 1] = true;
									}
								}
								for (int i = 1; i <= 9; i++) {
									if (_cells[secondcell].getLittleNumber(i)) {
										variousnumbers[i - 1] = true;
									}
								}
								for (int i = 1; i <= 9; i++) {
									if (_cells[thirdcell].getLittleNumber(i)) {
										variousnumbers[i - 1] = true;
									}
								}
								int tmpcount = 0;
								final int[] variousnumbers_int = new int[9];
								for (int index = 0; index < variousnumbers.length; index++) {
									if (variousnumbers[index]) {
										variousnumbers_int[tmpcount] = index;
										tmpcount++;
									}
								}
								if (tmpcount == 3) {
									for (int index = 0; index < 9; index++) {
										if (_cells[index].getStyle() == Cell.NONE
												&& index != firstcell
												&& index != secondcell
												&& index != thirdcell) {
											for (int index2 = 0; index2 < 3; index2++) {
												if (_cells[index]
														.getLittleNumber(variousnumbers_int[index2] + 1)) {
													_cells[index]
															.setLittleNumbers(
																	variousnumbers_int[index2] + 1,
																	false);
//													if (_sudoku.isPrintlog()) {
//														System.out
//																.println("new_cannumbers_found: "
//																		+ (variousnumbers_int[index2] + 1)
//																		+ ", "
//																		+ _cells[index]
//																				.getCol()
//																		+ "_"
//																		+ _cells[index]
//																				.getRow());
//													}
													_sudoku
															.increaseComplication(
																	SolveLogic.COMPLICATION_6_NAKEDTRIPLES,
																	(variousnumbers_int[index2] + 1),
																	_cells[index]
																			.getCol(),
																	_cells[index]
																			.getRow());
													solved = true;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return solved;
	}

	public boolean solve_NakedQuads() {
		boolean solved = false;
		for (int firstcell = 0; firstcell < 9; firstcell++) {
			if (_cells[firstcell].getStyle() == Cell.NONE) {
				for (int secondcell = firstcell + 1; secondcell < 9; secondcell++) {
					if (_cells[secondcell].getStyle() == Cell.NONE) {
						for (int thirdcell = secondcell + 1; thirdcell < 9; thirdcell++) {
							if (_cells[thirdcell].getStyle() == Cell.NONE) {
								for (int fourthcell = thirdcell + 1; fourthcell < 9; fourthcell++) {
									if (_cells[fourthcell].getStyle() == Cell.NONE) {
										final boolean[] variousnumbers = new boolean[9];
										for (int i = 0; i < variousnumbers.length; i++) {
											variousnumbers[i] = false;
										}
										for (int i = 1; i <= 9; i++) {
											if (_cells[firstcell]
													.getLittleNumber(i)) {
												variousnumbers[i - 1] = true;
											}
										}
										for (int i = 1; i <= 9; i++) {
											if (_cells[secondcell]
													.getLittleNumber(i)) {
												variousnumbers[i - 1] = true;
											}
										}
										for (int i = 1; i <= 9; i++) {
											if (_cells[thirdcell]
													.getLittleNumber(i)) {
												variousnumbers[i - 1] = true;
											}
										}
										for (int i = 1; i <= 9; i++) {
											if (_cells[fourthcell]
													.getLittleNumber(i)) {
												variousnumbers[i - 1] = true;
											}
										}
										int tmpcount = 0;
										final int[] variousnumbers_int = new int[9];
										for (int index = 0; index < variousnumbers.length; index++) {
											if (variousnumbers[index]) {
												variousnumbers_int[tmpcount] = index;
												tmpcount++;
											}
										}
										if (tmpcount == 4) {
											for (int index = 0; index < 9; index++) {
												if (_cells[index].getStyle() == Cell.NONE
														&& index != firstcell
														&& index != secondcell
														&& index != thirdcell
														&& index != fourthcell) {
													for (int index2 = 0; index2 < 4; index2++) {
														if (_cells[index]
																.getLittleNumber(variousnumbers_int[index2] + 1)) {
															_cells[index]
																	.setLittleNumbers(
																			variousnumbers_int[index2] + 1,
																			false);
//															if (_sudoku
//																	.isPrintlog()) {
//																System.out
//																		.println("new_cannumbers_found: "
//																				+ (variousnumbers_int[index2] + 1)
//																				+ ", "
//																				+ _cells[index]
//																						.getCol()
//																				+ "_"
//																				+ _cells[index]
//																						.getRow());
//															}
															_sudoku
																	.increaseComplication(
																			SolveLogic.COMPLICATION_7_NAKEDQUADS,
																			variousnumbers_int[index2] + 1,
																			_cells[index]
																					.getCol(),
																			_cells[index]
																					.getRow());
															solved = true;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return solved;
	}

	public int getLittleNumbersCount(final int currnumber) {
		// vozvrawaet coli4estvo v skolko cellov mogno postavit tekuwuyu tsifru
		int counter = 0;
		for (int currCell = 0; currCell < 9; currCell++) {
			if (_cells[currCell].isStyleNone()
					&& _cells[currCell].getLittleNumber(currnumber)) {
				counter++;
			}
		}
		return counter;
	}

	public Vector<Integer> getLittleNumbers(final int currnumber) {
		// vozvrawaet nomera cellov v kotorie mogno postavit tekuwuyu tsifru
		final Vector<Integer> vectorLitlleNumbers = new Vector<Integer>();
		for (int currCell = 0; currCell < 9; currCell++) {
			if (_cells[currCell].isStyleNone()
					&& _cells[currCell].getLittleNumber(currnumber)) {
				vectorLitlleNumbers.add(currCell);
			}
		}
		return vectorLitlleNumbers;
	}

	public boolean isValid() {
		// proveraet pravilnost rasstanovki tsifr(ne moget bit povtoreniy tsifr)
		final int[] tmpint = new int[9];
		for (int currCell = 0; currCell < 9; currCell++) {
			if (_cells[currCell].isStyleSolved()
					|| _cells[currCell].isStyleTask()
					|| _cells[currCell].isStyleLast()) {
				tmpint[_cells[currCell].getNumber() - 1]++;
			}
		}
		for (int currCell = 0; currCell < 9; currCell++) {
			if (tmpint[currCell] != 1) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		String tmpstring = "";
		for (int currCell = 0; currCell < 9; currCell++) {
			if (_cells[currCell].getStyle() != Cell.NONE) {
				tmpstring += _cells[currCell].getNumber();
			} else {
				tmpstring += "0";
			}
		}
		tmpstring += "\n";
		return tmpstring;
	}

	public void setLittleNumbersNot(final Vector<Integer> vectorNotLittleNumbers) {
		// vse littleNumbers iz vectorNotLittleNumbers ne mogut bit postavleni
		// ni v odin cell iz dannoy row/cell/bigcell
		for (int currNumber = 0; currNumber < vectorNotLittleNumbers.size(); currNumber++) {
			for (int currCell = 0; currCell < 9; currCell++) {
				_cells[currCell].setLittleNumbers(vectorNotLittleNumbers
						.get(currNumber), false);
			}
		}

	}

	public void generateLittleNumbersInCell(final int tmpcell) {
		// generiruet littleNumbers v tmpcell
		for (int currcell = 0; currcell < 9; currcell++) {
			if (currcell != tmpcell) {
				if (_cells[currcell].isStyleTask()) {
					_cells[tmpcell].setLittleNumbers(_cells[currcell]
							.getNumber(), false);
				}
			}
		}
	}

	public void setSudoku(final SudokuModel sudoku) {
		_sudoku = sudoku;
	}
}
