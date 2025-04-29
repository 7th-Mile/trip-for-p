package team.seventhmile.tripforp.domain.magazine.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.global.common.BaseEntity;

@Entity
@Table(name = "magazines")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Magazine extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "magazine_post_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	@ColumnDefault("0")
	private Integer views;

	@BatchSize(size = 100)
	@OneToMany(mappedBy = "magazine", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<MagazineFile> files = new ArrayList<>();

	public void update(String title, String content) {
		validateField(title, "Title");
		validateField(content, "Content");

		this.title = title;
		this.content = content;
	}

	public void incrementViews() {
		this.views += 1;
	}

	public void addFile(MagazineFile file) {
		this.files.add(file);
		file.setMagazine(this);
	}

	public void clearFile() {
		this.files.clear();
	}

	public static void validateField(String field, String fieldName) {
		if (field == null || field.isEmpty()) {
			throw new IllegalArgumentException(fieldName + "은(는) 필수 입력 항목입니다.");
		}
	}
}
