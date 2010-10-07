package jp.ac.keio.sfc.crew.dasaitama.model;
/*
 * ���[�g������ł̃I�v�V����������N���X�B�\�[�g���ƃ}�[�W���������Ă��邷��N���X�B
 */
public class DOption {
	private int margin;
	private String sort;
	/*
	 * 
	 */
	public DOption(String s,int m) {
		margin  = m;
		sort = s;
	}
	public int getMargin(){
		return margin;
	}
	public String getSort(){
		return sort;
	}

}
