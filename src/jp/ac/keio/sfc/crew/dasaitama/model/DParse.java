package jp.ac.keio.sfc.crew.dasaitama.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.util.Log;

/*
 * �p�[�X���邽�߂̃N���X�B
 */
public class DParse {
	/*
	 * �ϐ�
	 */

	private ArrayList<DRoute> route_list = new ArrayList<DRoute>();
	private ArrayList<String> location_list = new ArrayList<String>();
	private ArrayList<String> time_list = new ArrayList<String>();
	private ArrayList<String> transitInfo_list = new ArrayList<String>();
	private ArrayList<Integer> time_list_startIndex = new ArrayList<Integer>();
	private ArrayList<Integer> time_list_finishIndex = new ArrayList<Integer>();
	int basicInfo_startIndex;
	int basicInfo_finishIndex;
	private ArrayList<Integer> transitInfo_startIndex = new ArrayList<Integer>();
	private ArrayList<Integer> transitInfo_finishIndex = new ArrayList<Integer>();
	private ArrayList<Integer> location_list_startIndex = new ArrayList<Integer>();
	private ArrayList<Integer> location_list_finishIndex = new ArrayList<Integer>();
	private String basicInfo = new String();
	private Calendar cal;

	public DParse(String s, Calendar cal) {
		this.cal = cal;
		getDatas(s);


	}

	public ArrayList<DRoute> getRouteList() {
		if(route_list.size()>1){
			return route_list;
		}else{
			return null;
		}
		
	}

	/*
	 * @yokota ���[�g���쐬���郁�c�x
	 */
	private DRoute createRoute(){
		Calendar c = Calendar.getInstance(TimeZone.getDefault());
		c.setTime(cal.getTime());
		ArrayList<String> transit= new ArrayList<String>();
		ArrayList<String> location_from = new ArrayList<String>();
		ArrayList<String> location_to = new ArrayList<String>();
		ArrayList<String> time_from = new ArrayList<String>();
		ArrayList<String> time_to = new ArrayList<String>();
		if(transitInfo_list.size()>2){
			for(int lines=0;lines<transitInfo_list.size();lines+=1){
				transit.add(transitInfo_list.get(lines));
				location_from.add(location_list.get(lines*2));
				location_to.add(location_list.get(lines*2+1));
				time_from.add(time_list.get(lines*2));
				time_to.add(time_list.get(lines*2+1));
			}
			return new DRoute(basicInfo,transit,
					location_from,location_to,time_from,time_to,c);

		}else{
			return null;
		}
		
				
	}

	/*
	 * @yokota ���ꂼ��̃f�[�^�ɃC���f�b�N�X��U���Ă������\�b�h�B
	 */
	private void getDatas(String s) {
		char[] c = s.toCharArray();
		// * �ꕶ�������Ă����āA�f�[�^�ɃC���f�b�N�X��U��B
		int index = 0;
		while (index < c.length) {
			// *�H��f�[�^�̎n�܂�̏ꏊ���������Ƃ��B
			// 1)....</ta�Ȃǂ̉��T���B
			if (c[index] == ')') {
				index = findBaseInfo(c, index);
				index = findTdData(c, index);
				subStringDatas(s);
				if (!basicInfo.equals("")) {

					route_list.add(createRoute());
				}

				initializeValues();
			}
			index += 1;
			// ���肵����A���[�g���ЂƂł��Ă�

		}

	}

	/*
	 * @yokota �ϐ������������郁�\�b�h
	 */
	private void initializeValues() {
		basicInfo = "";
		location_list.clear();
		time_list.clear();
		transitInfo_list.clear();

		time_list_finishIndex.clear();
		time_list_startIndex.clear();
		location_list_finishIndex.clear();
		location_list_startIndex.clear();
		basicInfo_finishIndex = 0;
		basicInfo_startIndex = 0;
		transitInfo_startIndex.clear();
		transitInfo_finishIndex.clear();

	}

	/*
	 * @yokota �T�u�X�g�����O���郁�\�b�h
	 */
	private void subStringDatas(String s) {
		basicInfo = s.substring(basicInfo_startIndex, basicInfo_finishIndex);
		for (int i = 0; i < time_list_startIndex.size(); i += 1) {
			if (i % 2 == 0)
				transitInfo_list.add(s.substring(transitInfo_startIndex
						.get(i / 2), transitInfo_finishIndex.get(i / 2)));
			time_list.add(s.substring(time_list_startIndex.get(i),
					time_list_finishIndex.get(i)));
			location_list.add(s.substring(location_list_startIndex.get(i),
					location_list_finishIndex.get(i)));
		}
	}

	/*
	 * @yokota �ړ����̊�{�I�ȏ����擾����B
	 */
	private int findBaseInfo(char[] c, int index) {
		while (index < c.length) {
			if (c[index] == '/' && c[index + 1] == '>') {
				index += 2;
				index = findBaseInfoEnd(c, index);
				break;
			}
			index += 1;
		}
		return index;
	}

	/*
	 * @yokota �ړ����̊�{�I�ȏ��̏I����T���B �A��l�́A�T�����Ƃ���܂ł̈ʒu��� <b.......b
	 */
	private int findBaseInfoEnd(char[] c, int index) {
		int baseInfo_start_index = index;
		int baseInfo_finish_index = index;
		while (index < c.length) {
			if (c[index] == '<' && c[index + 1] == 'b' && c[index + 2] == 'r') {
				int temp = index;
				index += 2;
				if (c[index + 5] == 't' && c[index + 6] == 'a') {
					baseInfo_finish_index = temp;
					index += 6;
					basicInfo_startIndex = baseInfo_start_index;
					basicInfo_finishIndex = baseInfo_finish_index;
				}
				break;
			}
			index += 1;
		}

		return index;
	}

	/*
	 * @yokota td�^�O�̎n�܂��T�����\�b�h�B ���������炻�̂Ƃ��̕����̈ʒu��Ԃ��B
	 */
	private int findTdData(char[] c, int index) {
		// </table>�܂œ������B
		while (index < c.length) {
			if (c[index] == '/' && c[index + 1] == 't' && c[index + 2] == 'a') {
				// </table>�Ȃ̂ŁAwhile���u���C�N������B
				index += 2;
				break;
			} else if (c[index] == 'd' && c[index + 4] == 'd') {
				// </td><td>��T���B/td...d
				index += 6;
				index = judgeTdData(c, index);
			}
			index += 1;
		}
		return index;
	}

	/*
	 * @yokota td�^�O�̗v�f�𔻒肷��B���ԏ��Ȃ̂��H��芷�����Ȃ̂��H
	 * �T���I������ꏊ�܂ł̈ʒu����Ԃ��B
	 */
	private int judgeTdData(char[] c, int index) {
		/*
		 * <font>�Ŏn�܂��Ă���ƁA���Ԃ��A�ꏊ�̃f�[�^�ɂȂ�B �����łȂ���΁A�H����ɂȂ�B
		 */
		int transit_data_start_index = index;
		int transit_data_finish_index = index;
		while (index < c.length) {
			/*
			 * �������ꂸ��<br /></td>�����ꂽ�B
			 */
			if (c[index] == '<' && c[index + 1] == 'b') {
				transit_data_finish_index = index - 1;
				index += 11;
				transitInfo_startIndex.add(transit_data_start_index);
				transitInfo_finishIndex.add(transit_data_finish_index);
				break;
			} else if (c[index] == '#') {
				// #008000�Ȃǂ����������Ƃ��B
				index += 1;
				index = findTimeOrPlace(c, index);
				break;
			}
			index += 1;
		}

		return index;

	}

	/*
	 * @yokota ���ԏ��ƁA�ꏊ��T���o�����\�b�h�B
	 */
	private int findTimeOrPlace(char[] c, int index) {
		int has_no_time = 0, has_time = 1;
		int time_flagge = has_no_time;
		while (index < c.length) {
			if (c[index] == '8' && c[index + 2] == '"') {
				// #0000[80"]�Ƀ}�b�`����B
				index += 2;
				index = findLocation(c, index);
			} else if (c[index] == '0' && c[index + 2] == '"') {
				// #0080[00"]�Ƀ}�b�`����B
				index += 2;
				index = findTime(c, index);
				time_flagge = has_time;
			} else if (c[index] == '<' && c[index + 1] == 'b') {
				// * td�f�[�^�̏I���܂ł�����B
				index += 1;
				break;
			}
			index += 1;
		}
		// * �ЂƂ�<td>..<td>�̒��ɁA���Ԃ�����Ă��Ȃ���
		if (time_flagge == has_no_time) {
			// from->to�Ȃ̂œ�񕪁B
			for (int i = 0; i < 2; i += 1) {
				time_list_startIndex.add(0);
				time_list_finishIndex.add(0);
			}

		}
		return index;
	}

	/*
	 * @yokota �ꏊ��������Ă��郁�\�b�h �A��l�́A�T�����ꏊ�܂ł̃C���f�b�N�X
	 */
	private int findLocation(char[] c, int index) {

		while (index < c.length) {
			if (c[index] == '>') {
				index += 1;
				location_list_startIndex.add(index);
			} else if (c[index] == '<') {
				// �I���ɂ�����
				location_list_finishIndex.add(index);
				index += 1;
				break;
			}
			index += 1;
		}

		return index;
	}

	/*
	 * @yokota ���ԏ�������Ă��郁�\�b�h �A��l�́A�T�����ꏊ�܂ł̃C���f�b�N�X
	 */
	private int findTime(char[] c, int index) {
		while (index < c.length) {
			if (c[index] == '>') {
				index += 1;
				time_list_startIndex.add(index);
			} else if (c[index] == '<') {
				time_list_finishIndex.add(index);
				index += 1;
				break;
			}
			index += 1;
		}
		return index;
	}

}
