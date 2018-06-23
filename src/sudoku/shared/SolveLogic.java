package sudoku.shared;

import java.util.Vector;

public class SolveLogic {
	final public static int COMPLICATION_1_NAKEDSINGLE = 1;

	final public static int COMPLICATION_2_HIDDENSINGLE = 2;

	final public static int COMPLICATION_3_LOCKEDCANDIDATES1 = 3;

	final public static int COMPLICATION_4_LOCKEDCANDIDATES2 = 3;

	final public static int COMPLICATION_5_NAKEDPAIRS = 4;

	final public static int COMPLICATION_6_NAKEDTRIPLES = 5;

	final public static int COMPLICATION_7_NAKEDQUADS = 6;

	final public static int COMPLICATION_8_XWING = 7;

	final public static int COMPLICATION_9_BASEDONCOLORS = 8;

	final public static int COMPLICATION_10_SWORDFISH = 9;

	private SudokuModel _sudoku;

	private boolean _printLog = false;

	private Table _table;

	protected Vector<Solve> _vectorOfSolves = new Vector<Solve>();

	public SolveLogic(SudokuModel sudoku) {
		_sudoku = sudoku;
		_printLog = _sudoku.isPrintlog();
		_table = _sudoku.getTable();
	}

	public boolean Solve(int style) {
		// vibor stila solve
		switch (style) {
		case 0:
			return Solve_Loop();
		case 1:
			return solve_NakedSingle();
		case 2:
			return solve_HiddenSingle();
		case 3:
			return solve_LockedCandidates1();
		case 4:
			return solve_NakedPairs();
		case 5:
			return solve_NakedTriples();
		case 6:
			return solve_LockedCandidates2();
		case 7:
			return solve_NakedQuads();
		case 8:
			return solve_XWing();
		case 9:
			return solve_BasedOnColors();
		case 10:
			return solve_Swordfish();
		default:
			return false;
		}
	}

	public boolean SolveAll(boolean solveWithLoop) {
		// solveWithLoop=true => posle reweniya vsemi dostupnimi sredstavmi,
		// esli nvse ewe ne reweno, rewit podborom
		// rewit vse
		boolean finished = false;
		boolean solved = false;
		int starttime = (int) System.currentTimeMillis();
		int tmpcounter = 0;
		while (!finished) {
			if (_printLog) {
				System.out.println("New popitka started:" + tmpcounter);
			}
			finished = true;
			for (int i = 1; i <= 10; i++) {
				if (Solve(i) == true) {
					finished = false;
				}
			}
			tmpcounter++;
		}
		int endtime = (int) System.currentTimeMillis();
		if (_sudoku.isValid()) {
			if (_printLog) {
				System.out
						.println("Time for solving==" + (endtime - starttime));
			}
			solved = true;
		}

		return solved;
	}

	public void increaseComplication(int style, int currnumber, int col, int row) {
		// uveli4it slognost na slognost sootvetstvuyuwego reweniya
		_vectorOfSolves.add(new Solve(style, currnumber, col, row));
		if (_printLog) {
			System.out.println("new inclease complication");
		}
	}

	private boolean solve_NakedSingle() {
		if (_printLog) {
			System.out.println("Solve1_NakedSingle");
		}
		boolean solved = false;
		Vector<Integer> tmpCannumbers;
		boolean finished = false;
		while (!finished) {
			finished = true;
			for (int row = 0; row < 9; row++) {
				for (int col = 0; col < 9; col++) {
					tmpCannumbers = _table.getCell(col, row)
							.getLittleNumbersVector();
					if (_table.getCell(col, row).isStyleNone()
							&& tmpCannumbers.size() == 1) {
						if (_printLog) {
							System.out.println("new_solve_found: " + col + "_"
									+ row);
						}
						increaseComplication(COMPLICATION_1_NAKEDSINGLE,
								tmpCannumbers.get(0), col, row);
						_table.getCell(col, row)
								.setNumber(tmpCannumbers.get(0));
						_table.getCell(col, row).setStyle(Cell.SOLVED);
						_table.tableChanged(col, row);
						finished = false;
						solved = true;
					}
				}
			}
		}
		return solved;
	}

	private boolean solve_HiddenSingle() {
		boolean solved = false;
		if (_printLog) {
			System.out.println("Solve2_HiddenSingle");
		}
		boolean finished = false;
		while (!finished) {
			finished = true;
			if (finished) {
				for (int i = 0; i < 27; i++) {
					for (int curnumber = 1; curnumber <= 9; curnumber++) {
						if (_table.getAllRowsColsBigcells(i)
								.solve_HiddenSingle(curnumber)) {
							solved = true;
							finished = false;
						}
					}
				}
			}
		}
		return solved;
	}

	private boolean solve_LockedCandidates1() {
		boolean solved = false;
		if (_printLog) {
			System.out.println("Solve3_LockedCandidates1");
		}
		boolean finished = false;
		while (!finished) {
			finished = true;
			for (int currnumber = 1; currnumber <= 9; currnumber++) {
				for (int bigrow = 0; bigrow < 3; bigrow++) {
					for (int bigcol = 0; bigcol < 3; bigcol++) {
						int[] tmpcountrow = { 0, 0, 0 };
						int[] tmpcountcol = { 0, 0, 0 };

						for (int row = bigrow * 3; row < bigrow * 3 + 3; row++) {
							for (int col = bigcol * 3; col < bigcol * 3 + 3; col++) {
								if (_table.getCell(col, row).isStyleNone()
										&& _table.getCell(col, row)
												.getLittleNumber(currnumber)) {
									tmpcountcol[col - bigcol * 3]++;
									tmpcountrow[row - bigrow * 3]++;
								}
							}
						}
						for (int i = 0; i < 3; i++) {
							if (tmpcountrow[i] > 1
									&& tmpcountrow[1 - (i + 1) / 2] == 0
									&& tmpcountrow[2 - i / 2] == 0) {
								for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
									if ((tmpcol < bigcol * 3 || tmpcol >= bigcol * 3 + 3)
											&& _table.getCell(tmpcol,
													bigrow * 3 + i)
													.isStyleNone()
											&& _table
													.getCell(tmpcol,
															bigrow * 3 + i)
													.getLittleNumber(currnumber)) {
										_table.getCell(tmpcol, bigrow * 3 + i)
												.setLittleNumbers(currnumber,
														false);
										if (_printLog) {
											System.out
													.println("new_canNOTnumbers_found: "
															+ bigcol
															+ "_"
															+ bigrow);
										}
										increaseComplication(
												COMPLICATION_3_LOCKEDCANDIDATES1,
												currnumber, tmpcol, bigrow * 3
														+ i);
										solved = true;
									}
								}
							}
						}

						for (int i = 0; i < 3; i++) {
							if (tmpcountcol[i] > 1
									&& tmpcountcol[1 - (i + 1) / 2] == 0
									&& tmpcountcol[2 - i / 2] == 0) {
								for (int tmprow = 0; tmprow < 9; tmprow++) {
									if ((tmprow < bigrow * 3 || tmprow >= bigrow * 3 + 3)
											&& _table.getCell(bigcol * 3 + i,
													tmprow).isStyleNone()
											&& _table.getCell(bigcol * 3 + i,
													tmprow).getLittleNumber(
													currnumber)) {
										_table.getCell(bigcol * 3 + i, tmprow)
												.setLittleNumbers(currnumber,
														false);
										if (_printLog) {
											System.out
													.println("new_canNOTnumbers_found: "
															+ bigcol
															+ "_"
															+ bigrow);
										}
										increaseComplication(
												COMPLICATION_3_LOCKEDCANDIDATES1,
												currnumber, bigcol * 3 + i,
												tmprow);
										solved = true;
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

	private boolean solve_LockedCandidates2() {

		boolean solved = false;
		if (_printLog) {
			System.out.println("Solve4_LockedCandidates1");
		}
		boolean finished = false;
		while (!finished) {
			finished = true;
			for (int currnumber = 1; currnumber <= 9; currnumber++) {
				for (int bigrow = 0; bigrow < 3; bigrow++) {
					for (int bigcol = 0; bigcol < 3; bigcol++) {
						int[] tmpcountrow = { 0, 0, 0 };
						int[] tmpcountrow2 = { 0, 0, 0 };
						for (int row = bigrow * 3; row < bigrow * 3 + 3; row++) {
							for (int col = 0; col < 9; col++) {
								if ((col < bigcol * 3 || col >= bigcol * 3 + 3)) {

									if (_table.getCell(col, row).getStyle() == Cell.NONE
											&& _table
													.getCell(col, row)
													.getLittleNumber(currnumber)) {
										tmpcountrow[row - bigrow * 3]++;
									}
								} else {
									if (_table.getCell(col, row).getStyle() == Cell.NONE
											&& _table
													.getCell(col, row)
													.getLittleNumber(currnumber)) {
										tmpcountrow2[row - bigrow * 3]++;
									}
								}
							}
						}
						for (int i = 0; i < 3; i++) {
							if (tmpcountrow[i] == 0 && tmpcountrow2[i] != 0) {
								for (int tmprow = bigrow * 3; tmprow < bigrow * 3 + 3; tmprow++) {
									if (bigrow * 3 + i != tmprow) {
										for (int tmpcol = bigcol * 3; tmpcol < bigcol * 3 + 3; tmpcol++) {
											if (_table.getCell(tmpcol, tmprow)
													.getStyle() == Cell.NONE
													&& _table.getCell(tmpcol,
															tmprow)
															.getLittleNumber(
																	currnumber)) {
												_table.getCell(tmpcol, tmprow)
														.setLittleNumbers(
																currnumber,
																false);
												solved = true;
												if (_printLog) {
													System.out
															.println("new_canNOTnumbers_found: "
																	+ tmpcol
																	+ "_"
																	+ tmprow);
												}
												increaseComplication(
														COMPLICATION_4_LOCKEDCANDIDATES2,
														currnumber, tmpcol,
														tmprow);
											}
										}
									}
								}
							}
						}
						int[] tmpcountcol = { 0, 0, 0 };
						int[] tmpcountcol2 = { 0, 0, 0 };
						for (int col = bigcol * 3; col < bigcol * 3 + 3; col++) {
							for (int row = 0; row < 9; row++) {
								if ((row < bigrow * 3 || row >= bigrow * 3 + 3)) {
									if (_table.getCell(col, row).isStyleNone()
											&& _table
													.getCell(col, row)
													.getLittleNumber(currnumber)) {
										tmpcountcol[col - bigcol * 3]++;
									}
								} else {
									if (_table.getCell(col, row).isStyleNone()
											&& _table
													.getCell(col, row)
													.getLittleNumber(currnumber)) {
										tmpcountcol2[col - bigcol * 3]++;
									}
								}
							}
						}
						for (int i = 0; i < 3; i++) {
							if (tmpcountcol[i] == 0 && tmpcountcol2[i] != 0) {
								for (int tmpcol = bigcol * 3; tmpcol < bigcol * 3 + 3; tmpcol++) {
									if (bigcol * 3 + i != tmpcol) {
										for (int tmprow = bigrow * 3; tmprow < bigrow * 3 + 3; tmprow++) {
											if (_table.getCell(tmpcol, tmprow)
													.isStyleNone()
													&& _table.getCell(tmpcol,
															tmprow)
															.getLittleNumber(
																	currnumber)) {
												_table.getCell(tmpcol, tmprow)
														.setLittleNumbers(
																currnumber,
																false);
												solved = true;
												if (_printLog) {
													System.out
															.println("new_canNOTnumbers_found: "
																	+ tmpcol
																	+ "_"
																	+ tmprow);
												}
												increaseComplication(
														COMPLICATION_4_LOCKEDCANDIDATES2,
														currnumber, tmpcol,
														tmprow);
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

	private boolean solve_NakedPairs() {
		boolean solved = false;
		if (_printLog) {
			System.out.println("Solve5_NakedPairs");
		}
		boolean finished = false;
		while (!finished) {
			finished = true;
			if (finished) {
				for (int i = 0; i < 27; i++) {
					if (_table.getAllRowsColsBigcells(i).Solve_NakedPairs()) {
						solved = true;
						finished = false;
					}
				}
			}
		}
		return solved;
	}

	private boolean solve_NakedTriples() {
		boolean finished = false;
		if (_printLog) {
			System.out.println("Solve6_NakedTriples");
		}
		boolean solved = false;
		while (!finished) {
			finished = true;
			for (int i = 0; i < 27; i++) {
				if (_table.getAllRowsColsBigcells(i).solve_NakedTriples()) {
					finished = false;
					solved = true;
				}
			}
		}
		return solved;
	}

	private boolean solve_NakedQuads() {
		boolean finished = false;
		if (_printLog) {
			System.out.println("Solve7_NakedQuads");
		}
		boolean solved = false;
		while (!finished) {
			finished = true;
			if (finished) {
				for (int i = 0; i < 27; i++) {
					if (_table.getAllRowsColsBigcells(i).solve_NakedQuads()) {
						finished = false;
						solved = true;
					}
				}
			}
		}
		return solved;
	}

	private boolean solve_XWing() {
		boolean finished = false;
		if (_printLog) {
			System.out.println("Solve8_XWing");
		}
		boolean solved = false;
		while (!finished) {
			finished = true;

			if (finished) {
				for (int currnumber = 1; currnumber <= 9; currnumber++) {
					int[] tmpcountercol = new int[9];
					int[][] tmpwhere = new int[9][9];
					for (int col = 0; col < 9; col++) {
						for (int row = 0; row < 9; row++) {
							if (_table.getCell(col, row).isStyleNone()
									&& _table.getCell(col, row)
											.getLittleNumber(currnumber)) {
								tmpwhere[col][tmpcountercol[col]] = row;
								tmpcountercol[col]++;
							}
						}

					}
					int tmpcounter = 0;
					int[] tmpcountercol2 = new int[9];
					for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
						if (tmpcountercol[tmpcol] == 2) {
							tmpcountercol2[tmpcounter] = tmpcol;
							tmpcounter++;
						}
					}
					for (int firstcell = 0; firstcell < tmpcounter; firstcell++) {
						for (int secondcell = firstcell + 1; secondcell < tmpcounter; secondcell++) {
							if (finished
									&& tmpcounter >= 2
									&& tmpwhere[tmpcountercol2[firstcell]][0] == tmpwhere[tmpcountercol2[secondcell]][0]
									&& tmpwhere[tmpcountercol2[firstcell]][1] == tmpwhere[tmpcountercol2[secondcell]][1]) {
								for (int i = 0; i < 2; i++) {
									int tmprow = tmpwhere[tmpcountercol2[firstcell]][i];
									for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
										if (_table.getCell(tmpcol, tmprow)
												.isStyleNone()
												&& _table.getCell(tmpcol,
														tmprow)
														.getLittleNumber(
																currnumber)
												&& tmpcol != tmpcountercol2[firstcell]
												&& tmpcol != tmpcountercol2[secondcell]) {
											_table.getCell(tmpcol, tmprow)
													.setLittleNumbers(
															currnumber, false);
											if (_printLog) {
												System.out
														.println("new cannotnumbers found: "
																+ currnumber
																+ " "
																+ tmpcol
																+ "_" + tmprow);
											}
											increaseComplication(
													COMPLICATION_8_XWING,
													currnumber, tmpcol, tmprow);
											finished = false;
											solved = true;
										}
									}
								}
							}
						}
					}
				}
			}

			if (finished) {
				for (int currnumber = 1; currnumber <= 9; currnumber++) {
					int[] tmpcounterrow = new int[9];
					int[][] tmpwhere = new int[9][9];
					for (int row = 0; row < 9; row++) {
						for (int col = 0; col < 9; col++) {
							if (_table.getCell(col, row).isStyleNone()
									&& _table.getCell(col, row)
											.getLittleNumber(currnumber)) {
								tmpwhere[row][tmpcounterrow[row]] = col;
								tmpcounterrow[row]++;
							}
						}

					}
					int tmpcounter = 0;
					int[] tmpcounterrow2 = new int[9];
					for (int tmprow = 0; tmprow < 9; tmprow++) {
						if (tmpcounterrow[tmprow] == 2) {
							tmpcounterrow2[tmpcounter] = tmprow;
							tmpcounter++;
						}
					}
					for (int firstcell = 0; firstcell < tmpcounter; firstcell++) {
						for (int secondcell = firstcell + 1; secondcell < tmpcounter; secondcell++) {
							if (finished
									&& tmpcounter >= 2
									&& tmpwhere[tmpcounterrow2[firstcell]][0] == tmpwhere[tmpcounterrow2[secondcell]][0]
									&& tmpwhere[tmpcounterrow2[firstcell]][1] == tmpwhere[tmpcounterrow2[secondcell]][1]) {
								for (int i = 0; i < 2; i++) {
									int tmpcol = tmpwhere[tmpcounterrow2[firstcell]][i];
									for (int tmprow = 0; tmprow < 9; tmprow++) {
										if (_table.getCell(tmpcol, tmprow)
												.isStyleNone()
												&& _table.getCell(tmpcol,
														tmprow)
														.getLittleNumber(
																currnumber)
												&& tmprow != tmpcounterrow2[firstcell]
												&& tmprow != tmpcounterrow2[secondcell]) {
											_table.getCell(tmpcol, tmprow)
													.setLittleNumbers(
															currnumber, false);
											if (_printLog) {
												System.out
														.println("new cannotnumbers found: "
																+ currnumber
																+ " "
																+ tmpcol
																+ "_" + tmprow);
											}
											increaseComplication(
													COMPLICATION_8_XWING,
													currnumber, tmpcol, tmprow);
											finished = false;
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

		return false;
	}

	private boolean solve_BasedOnColors() {
		if (_printLog) {
			System.out.println("Solve9_BasedOnColors");
		}
		boolean solved = false;
		int[][] tmptruefalsemain = new int[9][9];
		for (int currnumber = 1; currnumber <= 9; currnumber++) {
			if (!solved) {
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						tmptruefalsemain[i][j] = 0;
					}
				}
				// 0==none
				// 1==yest
				// 2==false
				// 3==true
				for (int col = 0; col < 9; col++) {
					int tmpcounter = 0;
					for (int tmprow = 0; tmprow < 9; tmprow++) {
						if (_table.getCell(col, tmprow).isStyleNone()
								&& _table.getCell(col, tmprow).getLittleNumber(
										currnumber)) {
							tmpcounter++;
							tmptruefalsemain[col][tmprow] = 1;
						}
					}
					if (tmpcounter == 2) {
						// System.out.println("coll" + col);
					}
				}
				for (int row = 0; row < 9; row++) {
					int tmpcounter = 0;
					// Vector<Integer> tmpvector = new Vector<Integer>();
					for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
						if (_table.getCell(tmpcol, row).isStyleNone()
								&& _table.getCell(tmpcol, row).getLittleNumber(
										currnumber)) {
							tmpcounter++;
							// tmpvector.add(tmpcol);
							tmptruefalsemain[tmpcol][row] = 1;
						}
					}
					if (tmpcounter == 2) {
						// System.out.println("roww" + row);
					}
				}

				for (int bigrow = 0; bigrow < 3; bigrow++) {
					for (int bigcol = 0; bigcol < 3; bigcol++) {
						int tmpcounter = 0;
						for (int tmprow = bigrow * 3; tmprow < bigrow * 3 + 3; tmprow++) {
							// Vector<Integer> tmpvector = new
							// Vector<Integer>();
							for (int tmpcol = bigcol * 3; tmpcol < bigcol * 3 + 3; tmpcol++) {
								if (_table.getCell(tmpcol, tmprow)
										.isStyleNone()
										&& _table.getCell(tmpcol, tmprow)
												.getLittleNumber(currnumber)) {
									tmpcounter++;
									// tmpvector.add(tmpcol);
									tmptruefalsemain[tmpcol][tmprow] = 1;
								}
							}
							if (tmpcounter == 2) {
								// System.out.println("BIG" + bigcol + "_"
								// + bigrow);
							}
						}

					}
				}

				// String tmpstr = "";
				// for (int i = 0; i < 9; i++) {
				// for (int j = 0; j < 9; j++) {
				// tmpstr += tmptruefalsemain[j][i];
				// }
				// tmpstr += "\n";
				// }
				// System.out.println(tmpstr);
				int[][] tmptruefalse = new int[9][9];
				for (int startrow = 0; startrow < 9; startrow++) {
					for (int startcol = 0; startcol < 9; startcol++) {
						if (!solved) {
							for (int i = 0; i < 9; i++) {
								for (int j = 0; j < 9; j++) {
									tmptruefalse[i][j] = tmptruefalsemain[i][j];
								}
							}
							if ((_table.getRows(startrow)
									.getLittleNumbersCount(currnumber) == 2
									|| _table.getCol(startcol)
											.getLittleNumbersCount(currnumber) == 2 || _table
									.getBigCell(startrow / 3 * 3 + startcol / 3)
									.getLittleNumbersCount(currnumber) == 2)
									&& tmptruefalse[startcol][startrow] == 1) {
								tmptruefalse[startcol][startrow] = 3;
								// System.out.println("New cycle " + currnumber
								// + ", " + startcol + "_" + startrow);
								// tmpstr = "";
								// for (int i = 0; i < 9; i++) {
								// for (int j = 0; j < 9; j++) {
								// tmpstr += tmptruefalse[j][i];
								// }
								// tmpstr += "\n";
								// }
								// System.out.println(tmpstr);

								boolean finished = false;
								while (!finished) {
									finished = true;

									for (int tmprow = 0; tmprow < 9; tmprow++) {
										for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
											if (tmptruefalse[tmpcol][tmprow] == 2
													|| tmptruefalse[tmpcol][tmprow] == 3) {
												// System.out.println("wazup,
												// row "
												// +
												// tmprow);
												if (_table.getRows(tmprow)
														.getLittleNumbersCount(
																currnumber) == 2) {
													Vector<Integer> tmpvector = _table
															.getRows(tmprow)
															.getLittleNumbers(
																	currnumber);
													for (int i = 0; i < tmpvector
															.size(); i++) {
														if (tmpvector.get(i) != tmpcol
																&& tmptruefalse[tmpvector
																		.get(i)][tmprow] != 2
																&& tmptruefalse[tmpvector
																		.get(i)][tmprow] != 3) {
															if (tmptruefalse[tmpcol][tmprow] == 3) {
																tmptruefalse[tmpvector
																		.get(i)][tmprow] = 2;
															} else {
																tmptruefalse[tmpvector
																		.get(i)][tmprow] = 3;
															}
															finished = false;
														}
													}
												}
											}
										}
									}
									for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
										for (int tmprow = 0; tmprow < 9; tmprow++) {
											if (tmptruefalse[tmpcol][tmprow] == 2
													|| tmptruefalse[tmpcol][tmprow] == 3) {
												// System.out.println("wazup,
												// col "
												// +
												// tmpcol);
												if (_table.getCol(tmpcol)
														.getLittleNumbersCount(
																currnumber) == 2) {
													Vector<Integer> tmpvector = _table
															.getCol(tmpcol)
															.getLittleNumbers(
																	currnumber);
													for (int i = 0; i < tmpvector
															.size(); i++) {
														if (tmpvector.get(i) != tmprow
																&& tmptruefalse[tmpcol][tmpvector
																		.get(i)] != 2
																&& tmptruefalse[tmpcol][tmpvector
																		.get(i)] != 3) {

															if (tmptruefalse[tmpcol][tmprow] == 3) {
																tmptruefalse[tmpcol][tmpvector
																		.get(i)] = 2;
															} else {
																tmptruefalse[tmpcol][tmpvector
																		.get(i)] = 3;
															}
															finished = false;
														}
													}
												}
											}
										}
									}
									for (int bigrow = 0; bigrow < 3; bigrow++) {
										for (int bigcol = 0; bigcol < 3; bigcol++) {

											for (int tmprow = bigrow * 3; tmprow < bigrow * 3 + 3; tmprow++) {
												for (int tmpcol = bigcol * 3; tmpcol < bigcol * 3 + 3; tmpcol++) {
													if (tmptruefalse[tmpcol][tmprow] == 2
															|| tmptruefalse[tmpcol][tmprow] == 3) {
														if (_table
																.getBigCell(
																		bigrow
																				* 3
																				+ bigcol)
																.getLittleNumbersCount(
																		currnumber) == 2) {
															Vector<Integer> tmpvector = _table
																	.getBigCell(
																			bigrow
																					* 3
																					+ bigcol)
																	.getLittleNumbers(
																			currnumber);
															for (int i = 0; i < tmpvector
																	.size(); i++) {
																if (tmpvector
																		.get(i) != (tmprow - bigrow * 3)
																		* 3
																		+ (tmpcol - bigcol * 3)
																		&& tmptruefalse[tmpvector
																				.get(i)
																				% 3
																				+ bigcol
																				* 3][tmpvector
																				.get(i)
																				/ 3
																				+ bigrow
																				* 3] != 2
																		&& tmptruefalse[tmpvector
																				.get(i)
																				% 3
																				+ bigcol
																				* 3][tmpvector
																				.get(i)
																				/ 3
																				+ bigrow
																				* 3] != 3) {
																	if (tmptruefalse[tmpcol][tmprow] == 3) {
																		tmptruefalse[tmpvector
																				.get(i)
																				% 3
																				+ bigcol
																				* 3][tmpvector
																				.get(i)
																				/ 3
																				+ bigrow
																				* 3] = 2;
																	} else {
																		tmptruefalse[tmpvector
																				.get(i)
																				% 3
																				+ bigcol
																				* 3][tmpvector
																				.get(i)
																				/ 3
																				+ bigrow
																				* 3] = 3;
																	}
																	finished = false;
																}
															}
														}
													}
												}
											}
										}
									}
								}
								int wrongtype = 0;
								for (int row = 0; row < 9; row++) {
									int[] truefalse = new int[2];
									for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
										if (tmptruefalse[tmpcol][row] == 2) {
											truefalse[0]++;
										}
										if (tmptruefalse[tmpcol][row] == 3) {
											truefalse[1]++;
										}
									}
									if (truefalse[0] > 1) {
										wrongtype = 2;
									}
									if (truefalse[1] > 1) {
										wrongtype = 3;
									}
								}
								for (int col = 0; col < 9; col++) {
									int[] truefalse = new int[2];
									for (int tmprow = 0; tmprow < 9; tmprow++) {
										if (tmptruefalse[col][tmprow] == 2) {
											truefalse[0]++;
										}
										if (tmptruefalse[col][tmprow] == 3) {
											truefalse[1]++;
										}
									}
									if (truefalse[0] > 1) {
										wrongtype = 2;
									}
									if (truefalse[1] > 1) {
										wrongtype = 3;
									}
								}
								for (int bigrow = 0; bigrow < 3; bigrow++) {
									for (int bigcol = 0; bigcol < 3; bigcol++) {
										int[] truefalse = new int[2];
										for (int tmpcol = bigcol * 3; tmpcol < bigcol * 3 + 3; tmpcol++) {
											for (int tmprow = bigrow * 3; tmprow < bigrow * 3 + 3; tmprow++) {
												if (tmptruefalse[tmpcol][tmprow] == 2) {
													truefalse[0]++;
												}
												if (tmptruefalse[tmpcol][tmprow] == 3) {
													truefalse[1]++;
												}
											}
										}
										if (truefalse[0] > 1) {
											wrongtype = 2;
										}
										if (truefalse[1] > 1) {
											wrongtype = 3;
										}
									}
								}
								for (int tmprow = 0; tmprow < 9; tmprow++) {
									for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
										if (wrongtype != 0
												&& tmptruefalse[tmpcol][tmprow] == wrongtype) {
											_table.getCell(tmpcol, tmprow)
													.setLittleNumbers(
															currnumber, false);
											if (_printLog) {
												System.out
														.println("new_cannumbers_found: "
																+ currnumber
																+ ", "
																+ tmpcol
																+ "_" + tmprow);
											}
											increaseComplication(
													COMPLICATION_9_BASEDONCOLORS,
													currnumber, tmpcol, tmprow);
											solved = true;
										}
									}
								}
								if (!solved) {
									boolean wrong = false;
									for (int row = 0; row < 9; row++) {
										for (int col = 0; col < 9; col++) {
											if (tmptruefalse[col][row] == 1) {
												wrong = false;
												int tmptmptmp = 0;
												for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
													if (tmptruefalse[tmpcol][row] == 2) {
														if (tmptmptmp == 3) {
															wrong = true;
														} else {
															tmptmptmp = 2;
														}
													}
													if (tmptruefalse[tmpcol][row] == 3) {
														if (tmptmptmp == 2) {
															wrong = true;
														} else {
															tmptmptmp = 3;
														}
													}
												}
												for (int tmprow = 0; tmprow < 9; tmprow++) {
													if (tmptruefalse[col][tmprow] == 2) {
														if (tmptmptmp == 3) {
															wrong = true;
														} else {
															tmptmptmp = 2;
														}
													}
													if (tmptruefalse[col][tmprow] == 3) {
														if (tmptmptmp == 2) {
															wrong = true;
														} else {
															tmptmptmp = 3;
														}
													}
												}
												if (wrong == true) {
													_table.getCell(col, row)
															.setLittleNumbers(
																	currnumber,
																	false);
													if (_printLog) {
														System.out
																.println("new_cannumbers_found2: "
																		+ currnumber
																		+ ", "
																		+ col
																		+ "_"
																		+ row);
													}
													increaseComplication(
															COMPLICATION_9_BASEDONCOLORS,
															currnumber, col,
															row);
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

	private boolean solve_Swordfish() {
		boolean finished = false;
		if (_printLog) {
			System.out.println("Solve10_Swordfish");
		}
		boolean solved = false;
		while (!finished) {
			finished = true;
			for (int currnumber = 1; currnumber <= 9; currnumber++) {
				if (finished) {
					for (int firstrow = 0; firstrow < 9; firstrow++) {
						if (_table.getRows(firstrow).getLittleNumbersCount(
								currnumber) > 1
								&& _table.getRows(firstrow)
										.getLittleNumbersCount(currnumber) < 4) {
							for (int secondrow = firstrow + 1; secondrow < 9; secondrow++) {
								if (_table.getRows(secondrow)
										.getLittleNumbersCount(currnumber) > 1
										&& _table.getRows(secondrow)
												.getLittleNumbersCount(
														currnumber) < 4) {
									for (int thirdrow = secondrow + 1; thirdrow < 9; thirdrow++) {
										if (_table.getRows(thirdrow)
												.getLittleNumbersCount(
														currnumber) > 1
												&& _table.getRows(thirdrow)
														.getLittleNumbersCount(
																currnumber) < 4) {
											Vector<Integer> tmp1 = _table
													.getRows(firstrow)
													.getLittleNumbers(
															currnumber);
											Vector<Integer> tmp2 = _table
													.getRows(secondrow)
													.getLittleNumbers(
															currnumber);
											Vector<Integer> tmp3 = _table
													.getRows(thirdrow)
													.getLittleNumbers(
															currnumber);
											boolean[] tmptmp = new boolean[9];
											for (int i = 0; i < tmp1.size(); i++) {
												tmptmp[tmp1.get(i)] = true;
											}
											for (int i = 0; i < tmp2.size(); i++) {
												tmptmp[tmp2.get(i)] = true;
											}
											for (int i = 0; i < tmp3.size(); i++) {
												tmptmp[tmp3.get(i)] = true;
											}
											int tmpcounter = 0;
											for (int i = 0; i < 9; i++) {
												if (tmptmp[i] == true) {
													tmpcounter++;
												}
											}
											if (tmpcounter == 3) {
												for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
													if (tmptmp[tmpcol]) {
														for (int tmprow = 0; tmprow < 9; tmprow++) {
															if (tmprow != firstrow
																	&& tmprow != secondrow
																	&& tmprow != thirdrow
																	&& _table
																			.getCell(
																					tmpcol,
																					tmprow)
																			.getStyle() == Cell.NONE
																	&& _table
																			.getCell(
																					tmpcol,
																					tmprow)
																			.getLittleNumber(
																					currnumber)) {
																_table
																		.getCell(
																				tmpcol,
																				tmprow)
																		.setLittleNumbers(
																				currnumber,
																				false);
																solved = true;
																finished = false;
																if (_printLog) {
																	System.out
																			.println("new_cannumbers_found: "
																					+ currnumber
																					+ ", "
																					+ tmpcol
																					+ "_"
																					+ tmprow);
																}
																increaseComplication(
																		COMPLICATION_10_SWORDFISH,
																		currnumber,
																		tmpcol,
																		tmprow);
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
				if (finished) {
					for (int firstcol = 0; firstcol < 9; firstcol++) {
						if (_table.getCol(firstcol).getLittleNumbersCount(
								currnumber) > 1
								&& _table.getCol(firstcol)
										.getLittleNumbersCount(currnumber) < 4) {
							for (int secondcol = firstcol + 1; secondcol < 9; secondcol++) {
								if (_table.getCol(secondcol)
										.getLittleNumbersCount(currnumber) > 1
										&& _table.getCol(secondcol)
												.getLittleNumbersCount(
														currnumber) < 4) {
									for (int thirdcol = secondcol + 1; thirdcol < 9; thirdcol++) {
										if (_table.getCol(thirdcol)
												.getLittleNumbersCount(
														currnumber) > 1
												&& _table.getCol(thirdcol)
														.getLittleNumbersCount(
																currnumber) < 4) {
											Vector<Integer> tmp1 = _table
													.getCol(firstcol)
													.getLittleNumbers(
															currnumber);
											Vector<Integer> tmp2 = _table
													.getCol(secondcol)
													.getLittleNumbers(
															currnumber);
											Vector<Integer> tmp3 = _table
													.getCol(thirdcol)
													.getLittleNumbers(
															currnumber);
											boolean[] tmptmp = new boolean[9];
											for (int i = 0; i < tmp1.size(); i++) {
												tmptmp[tmp1.get(i)] = true;
											}
											for (int i = 0; i < tmp2.size(); i++) {
												tmptmp[tmp2.get(i)] = true;
											}
											for (int i = 0; i < tmp3.size(); i++) {
												tmptmp[tmp3.get(i)] = true;
											}
											int tmpcounter = 0;
											for (int i = 0; i < 9; i++) {
												if (tmptmp[i] == true) {
													tmpcounter++;
												}
											}
											if (tmpcounter == 3) {
												for (int tmprow = 0; tmprow < 9; tmprow++) {
													if (tmptmp[tmprow]) {
														for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
															if (tmpcol != firstcol
																	&& tmpcol != secondcol
																	&& tmpcol != thirdcol
																	&& _table
																			.getCell(
																					tmpcol,
																					tmprow)
																			.getStyle() == Cell.NONE
																	&& _table
																			.getCell(
																					tmpcol,
																					tmprow)
																			.getLittleNumber(
																					currnumber)) {
																_table
																		.getCell(
																				tmpcol,
																				tmprow)
																		.setLittleNumbers(
																				currnumber,
																				false);
																solved = true;
																finished = false;
																if (_printLog) {
																	System.out
																			.println("new_cannumbers_found: "
																					+ currnumber
																					+ ", "
																					+ tmpcol
																					+ "_"
																					+ tmprow);
																}
																increaseComplication(
																		COMPLICATION_10_SWORDFISH,
																		currnumber,
																		tmpcol,
																		tmprow);
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

		}

		return solved;
	}

	private boolean Solve_Loop() {
		if (_printLog) {
			System.out.println("Solve0_Loop");
		}
		SudokuModel tmpsudoku = _sudoku.clone();
		boolean finished = false;
		boolean solved = false;
		while (!finished) {
			finished = true;
			for (int row = 0; row < 9; row++) {
				for (int col = 0; col < 9; col++) {
					if (_table.getCell(col, row).getStyle() == Cell.NONE) {
						Vector<Integer> cannumbers = _table.getCell(col, row)
								.getLittleNumbersVector();
						for (int tmpnumber = 0; tmpnumber < cannumbers.size(); tmpnumber++) {
							_table.getCell(col, row).setNumber(
									cannumbers.get(tmpnumber));
							_table.getCell(col, row).setStyle(Cell.SOLVED);
							_table.tableChanged(col, row);
							SolveAll(false);
							if (!_sudoku.isValid()) {
								for (int tmprow = 0; tmprow < 9; tmprow++) {
									for (int tmpcol = 0; tmpcol < 9; tmpcol++) {
										_table.getCell(tmpcol, tmprow)
												.setNumber(
														tmpsudoku._table
																.getCell(
																		tmpcol,
																		tmprow)
																.getNumber());
										_table.getCell(tmpcol, tmprow)
												.setStyle(
														tmpsudoku._table
																.getCell(
																		tmpcol,
																		tmprow)
																.getStyle());
										_table
												.getCell(tmpcol, tmprow)
												.setLittleNumbers(
														tmpsudoku._table
																.getCell(
																		tmpcol,
																		tmprow)
																.getLettleNumbers());
									}
								}
								_table.tableChanged();
							} else {
								solved = true;
								return true;
							}
						}
					}
				}
			}
		}
		return false;

	}

	public int getComplication() {
		// polu4it slognost rewennogo krossvorda
		int tmpint = 0;
		for (int i = 0; i < _vectorOfSolves.size(); i++) {
			tmpint += _vectorOfSolves.get(i).getComplication();
		}
		return tmpint;
	}

	public Vector<Solve> getVectorOfSolves() {
		return _vectorOfSolves;
	}
}
