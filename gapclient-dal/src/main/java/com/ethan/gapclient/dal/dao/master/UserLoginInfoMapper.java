package com.ethan.gapclient.dal.dao.master;

import com.ethan.gapclient.dal.model.UserLoginInfo;

public interface UserLoginInfoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_login_info
     *
     * @mbggenerated Sat Jan 20 20:18:44 CST 2018
     */
    int deleteByPrimaryKey(Integer loginId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_login_info
     *
     * @mbggenerated Sat Jan 20 20:18:44 CST 2018
     */
    int insert(UserLoginInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_login_info
     *
     * @mbggenerated Sat Jan 20 20:18:44 CST 2018
     */
    int insertSelective(UserLoginInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_login_info
     *
     * @mbggenerated Sat Jan 20 20:18:44 CST 2018
     */
    UserLoginInfo selectByPrimaryKey(Integer loginId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_login_info
     *
     * @mbggenerated Sat Jan 20 20:18:44 CST 2018
     */
    int updateByPrimaryKeySelective(UserLoginInfo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_login_info
     *
     * @mbggenerated Sat Jan 20 20:18:44 CST 2018
     */
    int updateByPrimaryKey(UserLoginInfo record);
}