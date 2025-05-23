package com.itwill.lightbooks.domain;

import org.hibernate.annotations.DynamicInsert;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@DynamicInsert
@Getter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "NOVEL_GENRE")
public class NGenre {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "novel_id")
	@ToString.Exclude
	private Novel novel;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "genre_id")
	private Genre genre;
	
	@Column(name = "is_main")
	private int isMain;
	
}
