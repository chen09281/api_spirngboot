package com.api.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jakarta.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.api.mapper.ClassesMapper;
import com.api.mapper.ClassesUsersMapper;
import com.api.mapper.CreategsignMapper;
import com.api.mapper.CreatelsignMapper;
import com.api.mapper.CreatepsignMapper;
import com.api.mapper.CreatesignMapper;
import com.api.mapper.GsignMapper;
import com.api.mapper.LsignMapper;
import com.api.mapper.PsignMapper;
import com.api.mapper.SignMapper;
import com.api.mapper.UsersMapper;
import com.api.pojo.Classes;
import com.api.pojo.ClassesExample;
import com.api.pojo.ClassesUsers;
import com.api.pojo.ClassesUsersExample;
import com.api.pojo.Creategsign;
import com.api.pojo.CreategsignExample;
import com.api.pojo.Createlsign;
import com.api.pojo.CreatelsignExample;
import com.api.pojo.Createpsign;
import com.api.pojo.CreatepsignExample;
import com.api.pojo.Createsign;
import com.api.pojo.CreatesignExample;
import com.api.pojo.Gsign;
import com.api.pojo.GsignExample;
import com.api.pojo.Lsign;
import com.api.pojo.LsignExample;
import com.api.pojo.Psign;
import com.api.pojo.PsignExample;
import com.api.pojo.Sign;
import com.api.pojo.SignBean;
import com.api.pojo.SignExample;
import com.api.pojo.UserBean;
import com.api.pojo.Users;
import com.api.pojo.UsersExample;
import com.api.pojo.UsersExample.Criteria;
import com.api.utils.LocationUtils;


@Service
public class ApiService {

	Logger log = Logger.getLogger(ApiService.class);
	@Value("${format}")
	private String format;
	@Value("${key}")
	private String key;
	@Resource
	private UsersMapper usersMapper;
	@Resource
	private ClassesMapper classesMapper;
	@Resource
	private ClassesUsersMapper classesUsersMapper;
	@Resource
	private CreatesignMapper createsignMapper;
	@Resource
	private SignMapper signMapper;
	@Resource
	private CreatepsignMapper createpsignMapper;
	@Resource
	private PsignMapper psignMapper;
	@Resource
	private CreategsignMapper creategsignMapper;
	@Resource
	private GsignMapper gsignMapper;
	@Resource
	private CreatelsignMapper createlsignMapper;
	@Resource
	private LsignMapper lsignMapper;

	/**
	 * ???????????????
	 * @param openid
	 * @return
	 */
	public boolean addNewUser(String openid,String nickName,String sex){
		Users user = getUserInfo(openid);
		if(user!=null){
			log.info("?????????openid:"+openid+"????????????");
			return false;
		}
		user = new Users();
		user.setOpenid(openid);
		user.setPhone("0");
		user.setSex(sex);
		user.setNickName(nickName);
		int result = usersMapper.insert(user);
		if(result>0){
			log.info("??????????????????openid:"+openid);
			return true;
		}
		log.info("?????????????????????");
		return false;
	}

	public Users getUserInfo(String openid){
		if(openid==null||openid.equals("")){
			return null;
		}
		UsersExample ue = new UsersExample();
		Criteria c= new UsersExample().createCriteria();
		c.andOpenidEqualTo(openid);
		ue.or(c );
		List<Users> list = usersMapper.selectByExample(ue);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	public boolean updUserSex(String openid,String sex){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}
		user.setSex(sex);
		int i = usersMapper.updateByPrimaryKey(user);
		if(i>0){
			return true;
		}

		return false;
	}

	public boolean updUserPhone(String openid,String phone){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}
		user.setPhone(phone);
		int i = usersMapper.updateByPrimaryKey(user);
		if(i>0){
			return true;
		}
		return false;
	}

	public boolean testInviCode(String code){
		if(code==null){
			return true;
		}
		ClassesExample example = new ClassesExample();
		ClassesExample.Criteria criteria = example.createCriteria();
		criteria.andCodeEqualTo(code);
		example.or(criteria);
		List<Classes> list = classesMapper.selectByExample(example );
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}

	public String getInviCode(){
		String[] vaild = new String[]{"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e",
				"f","g","h","i","g","k","l","m","n","o",
				"p","q","r","s","t","u","v","w","x","y","z",
				"A","B","C","D","E","F","G","H","I","J","K","L",
				"M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

		String rtnStr = "";
		Random random = new Random();
		for(int i=0;i<8;i++){
			rtnStr+=vaild[random.nextInt(vaild.length)];
		}
		return rtnStr;
	}

	/**
	 * ????????????
	 * @return inviCode
	 */
	public Classes insClass(int uid,String name){
		Classes cla = new Classes();
		cla.setUid(uid);
		cla.setName(name);
		cla.setNum(0);
		cla.setCreateTime(new Date());
		String code = getInviCode();
		while(testInviCode(code)){
			code = getInviCode();
		}
		cla.setCode(code);
		int i = classesMapper.insert(cla);
		if(i>0){
			return cla;
		}
		return null;
	}

	//??????openid?????????????????????????????????
	public List<Classes> selUserCreateClasses(String openid){
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		ClassesExample example = new ClassesExample();
		ClassesExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(user.getId());
		example.or(criteria);
		example.setOrderByClause("createTime desc");
		List<Classes> list = classesMapper.selectByExample(example );

		return list;
	}

	//??????cid ???????????????????????????
	public List<Users> selClassesUserInfo(int cid){
		ClassesUsersExample example = new ClassesUsersExample();
		ClassesUsersExample.Criteria criteria = example.createCriteria();
		criteria.andCidEqualTo(cid);
		example.or(criteria);
		List<ClassesUsers> list = classesUsersMapper.selectByExample(example );

		List<Users> userList = new ArrayList<Users>();
		if(list!=null){
			for (ClassesUsers cu : list) {
				Users user = usersMapper.selectByPrimaryKey(cu.getUid());
				if(user!=null){
					user.setNickName(cu.getNickName());
					userList.add(user);
				}
			}
		}
		return userList;
	}

	public List getClassUserInfo(String openid){
		List data = new ArrayList<>();
		List<Classes> claList = selUserCreateClasses(openid);
		if(claList!=null){
			for (Classes cla : claList) {
				Map map = new HashMap();
				map.put("class", cla);
				List<Users> list = selClassesUserInfo(cla.getId());
				map.put("users", list);
				data.add(map);
			}
		}
		return data;
	}

	public boolean delClass(String openid,int cid){
		if(cid<=0){
			return false;
		}

		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return false;
		}

		if(cla.getUid()!=null&&cla.getUid()==user.getId()){
			int i = classesMapper.deleteByPrimaryKey(cid);
			if(i>0){
				//????????????
				//1.??????????????????????????????
				delClassUserByCid(cid);
				//2.???????????????????????????
				delCreateSignByCid(cid);
				delCreateGSignByCid(cid);
				delCreatePSignByCid(cid);
				delCreateLSignByCid(cid);
				return true;
			}
		}

		return false;
	}

	public int delClassUserByCid(int cid){
		ClassesUsersExample example = new ClassesUsersExample();
		ClassesUsersExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.or(criteria);
		return classesUsersMapper.deleteByExample(example );
	}

	public List<Users> selectUser(){
		List<Users> usersList = usersMapper.selectUser();
		return usersList;
	}

	/**
	 * ??????????????????????????????
	 * ??????????????????????????????
	 * ???sign???????????????
	 * @param cid
	 * @return
	 */
	public int delCreateSignByCid(int cid){
		CreatesignExample example = new CreatesignExample();
		CreatesignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.or(criteria);
		List<Createsign> list = createsignMapper.selectByExample(example );
		for (Createsign cs : list) {
			createsignMapper.deleteByPrimaryKey(cs.getId());
			delSignBySignid(cs.getId());
		}
		return list.size();
	}

	public int delSignBySignid(int signid){
		SignExample example = new SignExample();
		SignExample.Criteria criteria = example.createCriteria().andCsidEqualTo(signid);
		example.or(criteria);
		return signMapper.deleteByExample(example );
	}

	public int delCreatePSignByCid(int cid){
		CreatepsignExample example = new CreatepsignExample();
		CreatepsignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.or(criteria);
		List<Createpsign> list = createpsignMapper.selectByExample(example );
		for (Createpsign cps : list) {
			createpsignMapper.deleteByPrimaryKey(cps.getId());
			delPSignBySignid(cps.getId());
		}
		return list.size();
	}

	public int delPSignBySignid(int signid){
		PsignExample example = new PsignExample();
		PsignExample.Criteria criteria = example.createCriteria().andCpsidEqualTo(signid);
		example.or(criteria);
		return psignMapper.deleteByExample(example );
	}

	public int delCreateGSignByCid(int cid){
		CreategsignExample example = new CreategsignExample();
		CreategsignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.or(criteria);
		List<Creategsign> list = creategsignMapper.selectByExample(example );
		for (Creategsign cgs : list) {
			creategsignMapper.deleteByPrimaryKey(cgs.getId());
			delGSignBySignid(cgs.getId());
		}
		return list.size();
	}

	public int delGSignBySignid(int signid){
		GsignExample example = new GsignExample();
		GsignExample.Criteria criteria = example.createCriteria().andCgsidEqualTo(signid);
		example.or(criteria);
		return gsignMapper.deleteByExample(example );
	}

	public int delCreateLSignByCid(int cid){
		CreatelsignExample example = new CreatelsignExample();
		CreatelsignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.or(criteria);
		List<Createlsign> list = createlsignMapper.selectByExample(example );
		for (Createlsign cls : list) {
			createlsignMapper.deleteByPrimaryKey(cls.getId());
			delLSignBySignid(cls.getId());
		}
		return list.size();
	}

	public int delLSignBySignid(int signid){
		LsignExample example = new LsignExample();
		LsignExample.Criteria criteria = example.createCriteria().andClsidEqualTo(signid);
		example.or(criteria);
		return lsignMapper.deleteByExample(example );
	}

	public Classes selClassByInviCode(String code){
		if(code==null||code.equals("")){
			return null;
		}

		ClassesExample example = new ClassesExample();
		ClassesExample.Criteria criteria = example.createCriteria();
		criteria.andCodeEqualTo(code);
		example.or(criteria);
		List<Classes> list = classesMapper.selectByExample(example );
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * ????????????-?????????????????????????????????????????????????????????????????????
	 * @param openid
	 * @param code
	 * @return
	 */
	public Map insClassUser(String openid,String code){
		Map map = new HashMap<>();
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		Classes cla = selClassByInviCode(code);
		if(cla==null){
			map.put("fail", "?????????????????????");
			return map;
		}

		if(user.getId()==cla.getUid()){
			map.put("fail", "????????????????????????????????????");
			return map;
		}

		//????????????????????????????????????
		ClassesUsers claUser = selClassesUsersByCidAndUid(cla.getId(), user.getId());
		if(claUser!=null){
			map.put("fail", "????????????"+cla.getName()+"???????????????????????????");
			return map;
		}

		ClassesUsers record = new ClassesUsers();
		record.setCid(cla.getId());
		record.setUid(user.getId());
		record.setNickName(user.getNickName());
		int i = classesUsersMapper.insert(record );
		if(i>0){
			map.put("name", cla.getName());
			updClassNum(cla.getId());
			return map;
		}

		return null;
	}

	public ClassesUsers selClassesUsersByCidAndUid(int cid,int uid){
		ClassesUsersExample example = new ClassesUsersExample();
		ClassesUsersExample.Criteria criteria = example.createCriteria();
		criteria.andCidEqualTo(cid);
		criteria.andUidEqualTo(uid);
		example.or(criteria);
		List<ClassesUsers> list = classesUsersMapper.selectByExample(example );
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	/**
	 * ??????uid???????????????????????????
	 * @param uid
	 * @return
	 */
	public List<ClassesUsers> selClassesUsersByuid(int uid){
		ClassesUsersExample example = new ClassesUsersExample();
		ClassesUsersExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		example.or(criteria);
		List<ClassesUsers> list = classesUsersMapper.selectByExample(example );
		return list;
	}

	public List selUserJoinClass(String openid){
		List data = new ArrayList<>();
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		List<ClassesUsers> list = selClassesUsersByuid(user.getId());
		for (ClassesUsers claUser : list) {
			Map map = new HashMap<>();
			map.put("user", claUser);
			Classes cla = classesMapper.selectByPrimaryKey(claUser.getCid());
			map.put("class", cla);
			data.add(map);
		}

		return data;
	}

	public boolean updUserClassNickName(String openid,int cid,String nickName){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		ClassesUsers claUser = selClassesUsersByCidAndUid(cid, user.getId());
		if(claUser==null){
			return false;
		}
		claUser.setNickName(nickName);
		int i = classesUsersMapper.updateByPrimaryKeySelective(claUser);
		if(i>0){
			return true;
		}
		return false;
	}

	/**
	 * ??????cid??????????????????????????????
	 * @param cid
	 */
	public void updClassNum(int cid){
		ClassesUsersExample example = new ClassesUsersExample();
		ClassesUsersExample.Criteria criteria = example.createCriteria();
		criteria.andCidEqualTo(cid);
		example.or(criteria);
		long num = classesUsersMapper.countByExample(example);

		Classes record = new Classes();
		record.setId(cid);
		record.setNum((int)num);
		classesMapper.updateByPrimaryKeySelective(record );
	}

	public boolean delClassUserByUidAndCid(int uid,int cid){
		ClassesUsersExample example = new ClassesUsersExample();
		ClassesUsersExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andCidEqualTo(cid);
		example.or(criteria);
		int i = classesUsersMapper.deleteByExample(example );
		if(i>0){
			updClassNum(cid);
			return true;
		}
		return false;
	}

	/**
	 * ????????????????????????
	 * @param openid
	 * @param cid
	 * @return
	 */
	public boolean stuDelClassUserByOpenidAndCid(String openid,int cid){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		boolean b = delClassUserByUidAndCid(user.getId(), cid);

		return b;
	}

	/**
	 * ?????????????????????????????????
	 * @param openid
	 * @param cid
	 * @param uid
	 * @return
	 */
	public boolean teaDelClassUser(String openid,int cid,int uid){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return false;
		}

		if(cla.getUid()!=user.getId()){
			return false;
		}

		boolean b = delClassUserByUidAndCid(uid, cid);

		return b;
	}

	/**
	 * ????????????????????????
	 * @param cid
	 * @param validTime
	 * @return ????????????id,????????????0
	 */
	public int insCreateSign(int cid,int validTime){
		Createsign record = new Createsign();
		record.setCid(cid);
		record.setCreateTime(new Date());
		record.setValidTime(validTime);
		int i = createsignMapper.insert(record );
		if(i>0){
			return record.getId();
		}
		return 0;
	}

	/**
	 * ????????????????????????
	 * @param csid
	 * @return
	 */
	public boolean insSign(int uid,int csid){
		Sign record = new Sign();
		record.setUid(uid);
		record.setCsid(csid);
		record.setFinish("0");
		record.setSignTime(new Date());
		int i = signMapper.insert(record );
		if(i>0){
			return true;
		}
		return false;
	}

	public boolean createSign(String openid,int cid,int validTime){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return false;
		}

		int csid = insCreateSign(cid, validTime);

		System.out.println("csid:"+csid);

		if(csid<=0){
			return false;
		}
		List<Users> list = selClassesUserInfo(cid);
		if(list==null||list.size()<=0){
			return true;
		}
		for (Users u : list) {
			boolean b = insSign(u.getId(), csid);
			if(!b){
				//TODO ??????
			}
		}

		return true;
	}

	/**
	 * ????????????????????????
	 * @param cid
	 * @param password
	 * @param validTime
	 * @return ????????????id,????????????0
	 */
	public int insCreatePSign(int cid,String password,int validTime){
		Createpsign record = new Createpsign();
		record.setCid(cid);
		record.setVaildTime(validTime);
		record.setCreateTime(new Date());
		record.setPassword(password);
		int i = createpsignMapper.insert(record );
		if(i>0){
			return record.getId();
		}
		return 0;
	}

	public boolean insPSign(int cpsid,int uid){
		Psign record = new Psign();
		record.setCpsid(cpsid);
		record.setUid(uid);
		record.setFinish("0");
		record.setSignTime(new Date());
		int i = psignMapper.insert(record );
		if(i>0){
			return true;
		}
		return false;
	}

	public boolean createPSign(int cid,String openid,String password,int validTime){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return false;
		}

		int cpsid = insCreatePSign(cid, password, validTime);
		if(cpsid<=0){
			return false;
		}
		List<Users> list = selClassesUserInfo(cid);
		if(list==null||list.size()<=0){
			return true;
		}

		for (Users u : list) {
			boolean b = insPSign(cpsid, u.getId());
			if(!b){
				//TODO ????????????
			}
		}
		return true;
	}

	/**
	 * ????????????id???????????????0
	 * @param cid
	 * @param uid
	 * @return
	 */
	public int insCreateGSign(int cid,int validTime,String password){
		Creategsign record = new Creategsign();
		record.setCid(cid);
		record.setCreateTime(new Date());
		record.setPassword(password);
		record.setValidTime(validTime);
		int i = creategsignMapper.insert(record );
		if(i>0){
			return record.getId();
		}
		return 0;
	}

	public boolean insGSign(int cgsid,int uid){
		Gsign record = new Gsign();
		record.setCgsid(cgsid);
		record.setFinish("0");
		record.setSignTime(new Date());
		record.setUid(uid);
		int i = gsignMapper.insert(record );
		if(i>0){
			return true;
		}
		return false;
	}

	public boolean createGSign(String openid,int cid,int validTime,String password){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return false;
		}

		int cgsid = insCreateGSign(cid, validTime, password);
		if(cgsid<=0){
			return false;
		}

		List<Users> list = selClassesUserInfo(cid);
		if(list==null||list.size()<=0){
			return true;
		}
		for (Users u : list) {
			boolean b = insGSign(cgsid, u.getId());
			if(!b){
				//TODO ????????????
			}
		}
		return true;
	}

	/**
	 * ????????????????????????
	 * @param cid
	 * @param vaildTime
	 * @param range
	 * @param latitude
	 * @param longitude
	 * @return ????????????id,????????????0
	 */
	public int insCreateLSign(int cid,int validTime,int range,double latitude,double longitude){
		Createlsign record = new Createlsign();
		record.setCid(cid);
		record.setCreateTime(new Date());
		record.setLatitude(latitude);
		record.setLongitude(longitude);
		record.setRadius(range);
		record.setValidTime(validTime);
		int i = createlsignMapper.insert(record );
		if(i>0){
			return record.getId();
		}
		return 0;
	}

	public boolean insLSign(int clsid,int uid){
		Lsign record = new Lsign();
		record.setClsid(clsid);
		record.setUid(uid);
		record.setFinish("0");
		record.setLatitude(new Double(0));
		record.setLongitude(new Double(0));
		record.setSignTime(new Date());
		int i = lsignMapper.insert(record );
		if(i>0){
			return true;
		}
		return false;
	}

	public boolean createLSign(int cid,String openid,int validTime,double latitude,
							   double longitude,int range){
		Users user = getUserInfo(openid);
		if(user==null){
			return false;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return false;
		}

		int clsid = insCreateLSign(cid, validTime, range, latitude, longitude);
		if(clsid<=0){
			return false;
		}

		List<Users> list = selClassesUserInfo(cid);
		if(list==null||list.size()<=0){
			return true;
		}

		for (Users u : list) {
			boolean b = insLSign(clsid, u.getId());
			if(!b){
				//TODO ????????????
			}
		}
		return true;
	}

	public List<Users> getOneClassUserInfo(String openid,int cid){
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		Classes cla = classesMapper.selectByPrimaryKey(cid);
		if(cla==null){
			return null;
		}
		if(cla.getUid()!=user.getId()){
			return null;
		}

		List<Users> list = selClassesUserInfo(cid);
		if(list==null){
			return null;
		}
		for (int i=0;i<list.size();i++) {
			ClassesUsers claUser = selClassesUsersByCidAndUid(cid, list.get(i).getId());
			list.get(i).setNickName(claUser.getNickName());
		}

		return list;
	}

	/**
	 * ??????uid??????????????????
	 * @param uid
	 * @return
	 */
	public List<Classes> selSignByUidAndFinishEq0(int uid){

		SignExample example = new SignExample();
		SignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andFinishEqualTo("0");
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		List<Sign> list = signMapper.selectByExample(example );

		List<Classes> claList = new ArrayList<>();
		for (Sign s : list) {
			Createsign cs = createsignMapper.selectByPrimaryKey(s.getCsid());
			Classes cla = classesMapper.selectByPrimaryKey(cs.getCid());
			Classes temp = new Classes();
			temp.setName(cla.getName());
			temp.setSignid(s.getCsid());
			claList.add(temp);
		}

		return claList;
	}


	public List<Classes> selPSignByUidAndFinishEq0(int uid){
		PsignExample example = new PsignExample();
		PsignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andFinishEqualTo("0");
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		List<Psign> list = psignMapper.selectByExample(example );

		List<Classes> claList = new ArrayList<>();
		for (Psign ps : list) {
			Createpsign cps = createpsignMapper.selectByPrimaryKey(ps.getCpsid());
			Classes cla = classesMapper.selectByPrimaryKey(cps.getCid());
			Classes temp = new Classes();
			temp.setName(cla.getName());
			temp.setSignid(ps.getCpsid());
			claList.add(temp);
		}
		return claList;
	}

	public List<Classes> selGSignByUidAndFinishEq0(int uid){
		GsignExample example = new GsignExample();
		GsignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andFinishEqualTo("0");
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		List<Gsign> list = gsignMapper.selectByExample(example );

		List<Classes> claList = new ArrayList<>();
		for (Gsign gs : list) {
			Creategsign cgs = creategsignMapper.selectByPrimaryKey(gs.getCgsid());
			Classes cla = classesMapper.selectByPrimaryKey(cgs.getCid());
			Classes temp = new Classes();
			temp.setName(cla.getName());
			temp.setSignid(gs.getCgsid());
			claList.add(temp);
		}
		return claList;
	}

	public List<Classes> selLSignByUidAndFinishEq0(int uid){
		LsignExample example = new LsignExample();
		LsignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		criteria.andFinishEqualTo("0");
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		List<Lsign> list = lsignMapper.selectByExample(example );

		List<Classes> claList = new ArrayList<>();
		for (Lsign ls : list) {
			Createlsign cls = createlsignMapper.selectByPrimaryKey(ls.getClsid());
			Classes cla = classesMapper.selectByPrimaryKey(cls.getCid());
			Classes temp = new Classes();
			temp.setName(cla.getName());
			temp.setSignid(ls.getClsid());
			claList.add(temp);
		}
		return claList;
	}

	public Map getSignMsg(String openid){
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}
		Map map = new HashMap<>();
		List<Classes> list1 = selSignByUidAndFinishEq0(user.getId());
		List<Classes> list2 = selPSignByUidAndFinishEq0(user.getId());
		List<Classes> list3 = selGSignByUidAndFinishEq0(user.getId());
		List<Classes> list4 = selLSignByUidAndFinishEq0(user.getId());
		map.put("sign", list1);
		map.put("psign", list2);
		map.put("gsign", list3);
		map.put("lsign", list4);
		return map;
	}

	public boolean updSignBySignidAndUid(int signid,int uid,String finish,Date date){
		SignExample example = new SignExample();
		SignExample.Criteria criteria = example.createCriteria();
		criteria.andCsidEqualTo(signid).andUidEqualTo(uid);
		example.or(criteria);
		Sign record = new Sign();
		record.setFinish(finish);
		record.setSignTime(date);
		int i = signMapper.updateByExampleSelective(record , example );
		if(i>0){
			return true;
		}
		return false;
	}

	public Map sign(String openid,int signid){
		Map map = new HashMap<>();
		Users user = getUserInfo(openid);
		if(user==null){
			System.out.println(1);
			return null;
		}

		Createsign cs = createsignMapper.selectByPrimaryKey(signid);
		if(cs==null){
			System.out.println(2);
			return null;
		}

		Date date = new Date();
		if(cs.getValidTime()!=0){
			if(date.getTime()-cs.getCreateTime().getTime()>(cs.getValidTime()*1000)){
				map.put("overtime", "true");
				updSignBySignidAndUid(signid, user.getId(),"2",date);
				return map;
			}
		}
		map.put("date", new SimpleDateFormat(format).format(date));
		boolean b = updSignBySignidAndUid(signid, user.getId(),"1",date);
		if(b){
			return map;
		}
		System.out.println(3);
		return null;
	}

	public boolean updPSignBySignidAndUid(int signid,int uid,String finish,Date date){
		PsignExample example = new PsignExample();
		PsignExample.Criteria criteria = example.createCriteria()
				.andCpsidEqualTo(signid)
				.andUidEqualTo(uid);
		example.or(criteria);
		Psign record = new Psign();
		record.setFinish(finish);
		record.setSignTime(date);
		int i = psignMapper.updateByExampleSelective(record , example );
		if(i>0){
			return true;
		}
		return false;
	}

	public Map psign(int signid,String openid,String password){
		Map map = new HashMap<>();
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		Createpsign cps = createpsignMapper.selectByPrimaryKey(signid);
		if(cps==null){
			return null;
		}

		Date date = new Date();
		if(cps.getVaildTime()!=0){
			if(date.getTime()-cps.getCreateTime().getTime()>(cps.getVaildTime()*1000)){
				map.put("overtime", "true");
				updPSignBySignidAndUid(signid, user.getId(),"2",date);
				return map;
			}
		}

		if(!password.equals(cps.getPassword())){
			map.put("wrongPwd", "true");
			return map;
		}

		map.put("date", new SimpleDateFormat(format).format(date));
		boolean b = updPSignBySignidAndUid(signid, user.getId(),"1",date);
		if(b){
			return map;
		}
		return null;
	}

	public boolean updGSignBySignidAndUid(int signid,int uid,String finish,Date date){
		GsignExample example = new GsignExample();
		GsignExample.Criteria criteria = example.createCriteria()
				.andCgsidEqualTo(signid)
				.andUidEqualTo(uid);
		example.or(criteria);
		Gsign record = new Gsign();
		record.setFinish(finish);
		record.setSignTime(date);
		int i = gsignMapper.updateByExampleSelective(record , example );
		if(i>0){
			return true;
		}
		return false;
	}

	public Map gsign(int signid,String openid,String password){
		Map map = new HashMap<>();
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		Creategsign cgs = creategsignMapper.selectByPrimaryKey(signid);
		if(cgs==null){
			return null;
		}

		Date date = new Date();
		if(cgs.getValidTime()!=0){
			if(date.getTime()-cgs.getCreateTime().getTime()>(cgs.getValidTime()*1000)){
				map.put("overtime", "true");
				updGSignBySignidAndUid(signid, user.getId(),"2",date);
				return map;
			}
		}

		if(!password.equals(cgs.getPassword())){
			map.put("wrongPwd", "true");
			return map;
		}

		map.put("date", new SimpleDateFormat(format).format(date));
		boolean b = updGSignBySignidAndUid(signid, user.getId(),"1",date);
		if(b){
			return map;
		}
		return null;
	}

	public boolean updLSign(int signid,int uid,
							double latitude,double longitude,
							String finish,Date date){
		LsignExample example = new LsignExample();
		LsignExample.Criteria criteria = example.createCriteria().andClsidEqualTo(signid).andUidEqualTo(uid);
		example.or(criteria);
		Lsign record = new Lsign();
		record.setFinish(finish);
		record.setSignTime(date);
		record.setLatitude(latitude);
		record.setLongitude(longitude);
		record.setLocation(LocationUtils.reverseLocation(latitude, longitude, this.key));
		int i = lsignMapper.updateByExampleSelective(record , example );
		if(i>0){
			return true;
		}
		return false;
	}

	public Map lsign(String openid,int signid,double latitude,double longitude){
		Map map = new HashMap<>();
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		Createlsign cls = createlsignMapper.selectByPrimaryKey(signid);
		if(cls==null){
			return null;
		}
		Date date = new Date();
		if(cls.getValidTime()!=0){
			if(date.getTime()-cls.getCreateTime().getTime()>(cls.getValidTime()*1000)){
				map.put("overtime", "true");
				updLSign(signid, user.getId(), latitude, longitude,"2",date);
				return map;
			}
		}

		double dis = LocationUtils.getDisrtance(latitude, longitude, cls.getLatitude(), cls.getLongitude());
		if(cls.getRadius()!=0&&dis>cls.getRadius()){
			map.put("overRange", "true");
			return map;
		}

		map.put("date", new SimpleDateFormat(format).format(date));
		boolean b = updLSign(signid, user.getId(), latitude, longitude,"1",date);
		if(b){
			return map;
		}
		return null;
	}

	public Createlsign getLsignRange(int signid){
		//TODO ?????????
		return createlsignMapper.selectByPrimaryKey(signid);
	}

	public List<Createsign> selCreatesignBycid(int cid){
		CreatesignExample example = new CreatesignExample();
		CreatesignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.setOrderByClause("createTime desc");
		example.or(criteria);
		return createsignMapper.selectByExample(example );
	}

	public List<Createpsign> selCreatePSignByCid(int cid){
		CreatepsignExample example = new CreatepsignExample();
		CreatepsignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.setOrderByClause("createTime desc");
		example.or(criteria);
		return createpsignMapper.selectByExample(example );
	}

	public List<Creategsign> selCreategsignByCid(int cid){
		CreategsignExample example = new CreategsignExample();
		CreategsignExample.Criteria c = example.createCriteria().andCidEqualTo(cid);
		example.setOrderByClause("createTime desc");
		example.or(c);
		return creategsignMapper.selectByExample(example );
	}

	public List<Createlsign> selCreateLsignByCid(int cid){
		CreatelsignExample example = new CreatelsignExample();
		CreatelsignExample.Criteria criteria = example.createCriteria().andCidEqualTo(cid);
		example.setOrderByClause("createTime desc");
		example.or(criteria);
		return createlsignMapper.selectByExample(example );
	}

	public List<Sign> selSignBySignid(int signid){
		SignExample example = new SignExample();
		SignExample.Criteria criteria = example.createCriteria()
				.andCsidEqualTo(signid);
		example.setOrderByClause("signTime");
		example.or(criteria);
		return signMapper.selectByExample(example );
	}

	public List<Psign> selPSignBySignid(int signid){

		PsignExample example = new PsignExample();
		PsignExample.Criteria criteria = example.createCriteria()
				.andCpsidEqualTo(signid);
		example.or(criteria);
		example.setOrderByClause("signTime");
		return psignMapper.selectByExample(example );
	}

	public List<Gsign> selGSignBySignid(int signid){
		GsignExample example = new GsignExample();
		GsignExample.Criteria criteria = example.createCriteria().andCgsidEqualTo(signid);
		example.or(criteria);
		example.setOrderByClause("signTime");
		return gsignMapper.selectByExample(example );
	}

	public List<Lsign> selLSignBySignid(int signid){
		LsignExample example = new LsignExample();
		LsignExample.Criteria cri = example.createCriteria().andClsidEqualTo(signid);
		example.or(cri);
		example.setOrderByClause("signTime");
		return lsignMapper.selectByExample(example );
	}

	public Map getSignResult(String openid){
		Map map = new HashMap<>();
		List<Classes> claList = selUserCreateClasses(openid);
		if(claList==null||claList.size()<=0){
			return null;
		}

		List<SignBean> sList = new ArrayList<>();
		List<SignBean> pList = new ArrayList<>();
		List<SignBean> gList = new ArrayList<>();
		List<SignBean> lList = new ArrayList<>();

		for (Classes cla : claList) {

			//??????????????????
			List<Createsign> csList = selCreatesignBycid(cla.getId());
			//???????????????????????????
			for (Createsign cs : csList) {
				SignBean sb = new SignBean();
				sb.setCreateTime(cs.getCreateTime());
				sb.setName(cla.getName());
				sb.setValidTime(cs.getValidTime());

				List<UserBean> ubList = new ArrayList<>();
				//?????????????????????????????????
				List<Sign> list = selSignBySignid(cs.getId());
				for (Sign sign : list) {
					UserBean ub = new UserBean();
					ub.setResult(sign.getFinish());
					ub.setSignTime(sign.getSignTime());

					Users user = usersMapper.selectByPrimaryKey(sign.getUid());
					ub.setSex(user.getSex());
					ub.setPhone(user.getPhone());

					ClassesUsers cu = selClassesUsersByCidAndUid(cla.getId(), sign.getUid());
					ub.setNickName(cu.getNickName());
					ubList.add(ub);
				}
				sb.setUsers(ubList);
				sList.add(sb);
			}

			//??????????????????
			List<Createpsign> cpsList = selCreatePSignByCid(cla.getId());
			for (Createpsign cps : cpsList) {
				SignBean sb = new SignBean();
				sb.setCreateTime(cps.getCreateTime());
				sb.setName(cla.getName());
				sb.setValidTime(cps.getVaildTime());

				List<UserBean> ubList = new ArrayList<>();
				//?????????????????????????????????
				List<Psign> list = selPSignBySignid(cps.getId());
				for (Psign psign : list) {
					UserBean ub = new UserBean();
					ub.setResult(psign.getFinish());
					ub.setSignTime(psign.getSignTime());

					Users user = usersMapper.selectByPrimaryKey(psign.getUid());
					ub.setSex(user.getSex());
					ub.setPhone(user.getPhone());

					ClassesUsers cu = selClassesUsersByCidAndUid(cla.getId(), psign.getUid());
					ub.setNickName(cu.getNickName());
					ubList.add(ub);
				}
				sb.setUsers(ubList);
				pList.add(sb);
			}

			//??????????????????
			List<Creategsign> cgsList = selCreategsignByCid(cla.getId());
			for (Creategsign cgs : cgsList) {
				SignBean sb = new SignBean();
				sb.setCreateTime(cgs.getCreateTime());
				sb.setName(cla.getName());
				sb.setValidTime(cgs.getValidTime());

				List<UserBean> ubList = new ArrayList<>();
				//?????????????????????????????????
				List<Gsign> list = selGSignBySignid(cgs.getId());
				for (Gsign gsign : list) {
					UserBean ub = new UserBean();
					ub.setResult(gsign.getFinish());
					ub.setSignTime(gsign.getSignTime());

					Users user = usersMapper.selectByPrimaryKey(gsign.getUid());
					ub.setSex(user.getSex());
					ub.setPhone(user.getPhone());

					ClassesUsers cu = selClassesUsersByCidAndUid(cla.getId(), gsign.getUid());
					ub.setNickName(cu.getNickName());
					ubList.add(ub);
				}
				sb.setUsers(ubList);
				gList.add(sb);
			}

			//??????????????????
			List<Createlsign> clsList = selCreateLsignByCid(cla.getId());
			for (Createlsign cls : clsList) {
				SignBean sb = new SignBean();
				sb.setCreateTime(cls.getCreateTime());
				sb.setName(cla.getName());
				sb.setValidTime(cls.getValidTime());

				List<UserBean> ubList = new ArrayList<>();
				//?????????????????????????????????
				List<Lsign> list = selLSignBySignid(cls.getId());
				for (Lsign lsign : list) {
					UserBean ub = new UserBean();
					ub.setResult(lsign.getFinish());
					ub.setLocation(lsign.getLocation());
					ub.setSignTime(lsign.getSignTime());

					Users user = usersMapper.selectByPrimaryKey(lsign.getUid());
					ub.setSex(user.getSex());
					ub.setPhone(user.getPhone());

					ClassesUsers cu = selClassesUsersByCidAndUid(cla.getId(), lsign.getUid());
					ub.setNickName(cu.getNickName());
					ubList.add(ub);
				}
				sb.setUsers(ubList);
				lList.add(sb);
			}

		}

		map.put("sign", sList);
		map.put("psign", pList);
		map.put("gsign", gList);
		map.put("lsign", lList);
		map.put("total", sList.size()+pList.size()+gList.size()+lList.size());
		return map;
	}


	public List<Sign> selSignByUid(int uid){
		SignExample example = new SignExample();
		SignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		return signMapper.selectByExample(example );
	}

	public List<Psign> selPSignByUid(int uid){
		PsignExample example = new PsignExample();
		PsignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		return psignMapper.selectByExample(example );
	}

	public List<Gsign> selGsignByUid(int uid){
		GsignExample example = new GsignExample();
		GsignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		return gsignMapper.selectByExample(example );
	}

	public List<Lsign> selLsignByUid(int uid){
		LsignExample example = new LsignExample();
		LsignExample.Criteria criteria = example.createCriteria();
		criteria.andUidEqualTo(uid);
		example.or(criteria);
		example.setOrderByClause("signTime desc");
		return lsignMapper.selectByExample(example );
	}

	public Map getJoinedSign(String openid){
		Map map = new HashMap<>();
		Users user = getUserInfo(openid);
		if(user==null){
			return null;
		}

		List<SignBean> sList = new ArrayList<>();
		List<SignBean> pList = new ArrayList<>();
		List<SignBean> gList = new ArrayList<>();
		List<SignBean> lList = new ArrayList<>();

		//????????????
		List<Sign> list1 = selSignByUid(user.getId());
		for (Sign sign : list1) {
			SignBean sb = new SignBean();

			Createsign cs = createsignMapper.selectByPrimaryKey(sign.getCsid());
			sb.setCreateTime(cs.getCreateTime());
			Classes cla = classesMapper.selectByPrimaryKey(cs.getCid());
			if(cla==null){
				continue;
			}
			sb.setName(cla.getName());

			UserBean ub = new UserBean();
			ub.setSignTime(sign.getSignTime());
			ub.setResult(sign.getFinish());
			sb.setUser(ub);

			sList.add(sb);
		}

		//????????????
		List<Psign> list2 = selPSignByUid(user.getId());
		for (Psign psign : list2) {
			SignBean sb = new SignBean();

			Createpsign cps = createpsignMapper.selectByPrimaryKey(psign.getCpsid());
			sb.setCreateTime(cps.getCreateTime());
			Classes cla = classesMapper.selectByPrimaryKey(cps.getCid());
			if(cla==null){
				continue;
			}
			sb.setName(cla.getName());

			UserBean ub = new UserBean();
			ub.setSignTime(psign.getSignTime());
			ub.setResult(psign.getFinish());
			sb.setUser(ub);

			pList.add(sb);
		}

		//????????????
		List<Gsign> list3 = selGsignByUid(user.getId());
		for (Gsign gsign : list3) {
			SignBean sb = new SignBean();

			Creategsign cgs = creategsignMapper.selectByPrimaryKey(gsign.getCgsid());
			sb.setCreateTime(cgs.getCreateTime());
			Classes cla = classesMapper.selectByPrimaryKey(cgs.getCid());
			if(cla==null){
				continue;
			}
			sb.setName(cla.getName());

			UserBean ub = new UserBean();
			ub.setSignTime(gsign.getSignTime());
			ub.setResult(gsign.getFinish());
			sb.setUser(ub);

			gList.add(sb);
		}

		//????????????
		List<Lsign> list4 = selLsignByUid(user.getId());
		for (Lsign lsign : list4) {
			SignBean sb = new SignBean();

			Createlsign cls = createlsignMapper.selectByPrimaryKey(lsign.getClsid());
			sb.setCreateTime(cls.getCreateTime());
			Classes cla = classesMapper.selectByPrimaryKey(cls.getCid());
			if(cla==null){
				continue;
			}
			sb.setName(cla.getName());

			UserBean ub = new UserBean();
			ub.setSignTime(lsign.getSignTime());
			ub.setLocation(lsign.getLocation());
			ub.setResult(lsign.getFinish());
			sb.setUser(ub);

			lList.add(sb);
		}

		map.put("sign", sList);
		map.put("psign", pList);
		map.put("gsign", gList);
		map.put("lsign", lList);
		map.put("total", (sList.size()+pList.size()+gList.size()+lList.size()));
		return map;
	}
}
