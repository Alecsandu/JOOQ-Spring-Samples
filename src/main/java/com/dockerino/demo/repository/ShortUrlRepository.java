package com.dockerino.demo.repository;

import com.dockerino.demo.exception.ShortUrlNotFoundException;
import com.dockerino.demo.model.ShortUrl;
import com.dockerino.jooq.generated.tables.records.LinksRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static com.dockerino.jooq.generated.tables.Links.LINKS;

@CacheConfig("links")
@Repository
public class ShortUrlRepository {

    private final DSLContext dsl;

    public ShortUrlRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Cacheable(value = "database-SHORT", key = "#shortCode")
    public ShortUrl findByShortCode(Long shortCode) {
        LinksRecord record = dsl.selectFrom(LINKS)
                .where(LINKS.ID.eq(shortCode))
                .fetchOne();

        return mapRecordToShortUrl(record);
    }

    public Stream<ShortUrl> findAllByUserId(UUID userId) {
        Result<LinksRecord> records = dsl.selectFrom(LINKS)
                .where(LINKS.USER_ID.eq(userId))
                .orderBy(LINKS.CREATED_AT.desc())
                .fetch();
        return records.stream()
                .map(this::mapRecordToShortUrl);
    }

    public ShortUrl save(String originalUrl, UUID userId) {
        return dsl.insertInto(LINKS)
                .set(LINKS.ORIGINAL_URL, originalUrl)
                .set(LINKS.USER_ID, userId)
                .set(LINKS.CREATED_AT, OffsetDateTime.now())
                .returning()
                .fetchOne(this::mapRecordToShortUrl);
    }

    private ShortUrl mapRecordToShortUrl(LinksRecord r) {
        if (r == null) {
            throw new ShortUrlNotFoundException();
        }

        return new ShortUrl(r.getId(),
                r.getOriginalUrl(),
                r.getUserId(),
                r.getCreatedAt());
    }
}
