package com.itwill.lightbooks.domain;

import java.io.Serializable;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
@IdClass(NovelLikeId.class)
@Table(name = "NOVEL_LIKE")
public class NovelLike extends BaseTimeEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Id
	@ManyToOne
	@JoinColumn(name = "novel_id")
	private Novel novel;
}
