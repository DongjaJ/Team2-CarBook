package softeer.carbook.domain.post.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import softeer.carbook.domain.post.model.Image;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ImageRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ImageRepository(DataSource dataSource) { this.jdbcTemplate = new JdbcTemplate(dataSource); }

    public Image getImageByPostId(int postId){
        return jdbcTemplate.queryForObject("select * from IMAGE where post_id = ?",
                imageRowMapper(), postId);
    }

    public List<Image> getImagesOfRecentPosts(int size, int index) {
        return jdbcTemplate.query("SELECT img.post_id, img.image_url " +
                        "FROM POST AS p INNER JOIN IMAGE AS img ON p.id = img.post_id " +
                        "ORDER BY p.create_date DESC LIMIT ?, ?",
                imageRowMapper(), index, size);
    }

    private RowMapper<Image> imageRowMapper(){
        return (rs, rowNum) -> new Image(
                rs.getInt("post_id"),
                rs.getString("image_url")
        );
    }
}