package com.zlt.entity;

import com.zlt.annotation.Column;
import com.zlt.annotation.Id;
import com.zlt.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table("user")
public class User {

    @Id
    private Long uid;

    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String salt;
    @Column
    private String nickname;
    @Column
    private String regTime;
    @Column
    private String loginTime;
    @Column
    private String email;
    @Column
    private String photo;
}
