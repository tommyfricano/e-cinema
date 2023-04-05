package com.ecinema.models.movie;

import com.ecinema.models.show.Show;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue
    @Column(name = "movieID")
    private int movieID;

    @Column(name = "title")
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "cast")
    private String cast;

    @Column(name = "director")
    private String director;

    @Column(name = "producer")
    private String producer;

    @Column(name = "synopsis")
    private String synopsis;

    @Column(name = "review")
    private String review;

    @Column(name = "movieimage")
    private String movieImage;

    @Column(name = "trailervideo")
    private String trailerVideo;

    @Column(name = "rating")
    private String rating;

    @Column(name = "genre")
    private String genre;

    @OneToMany(mappedBy = "movie")
    private List<Show> showsTimes;

    public Movie(String title,
                 String category,
                 String cast,
                 String director,
                 String producer,
                 String synopsis,
                 String review,
                 String movieImage,
                 String trailerVideo,
                 String rating,
                 String genre) {
        this.title = title;
        this.category = category;
        this.cast = cast;
        this.director = director;
        this.producer = producer;
        this.synopsis = synopsis;
        this.review = review;
        this.movieImage = movieImage;
        this.trailerVideo = trailerVideo;
        this.rating = rating;
        this.genre = genre;
    }

}
