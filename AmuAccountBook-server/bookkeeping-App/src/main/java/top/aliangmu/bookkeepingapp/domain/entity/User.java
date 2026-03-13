package top.aliangmu.bookkeepingapp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("wechat_user")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String openid;
}
