package com.dockerino.demo.repository;

import com.dockerino.demo.exception.ShortUrlNotFoundException;
import com.dockerino.demo.model.ShortUrl;
import com.dockerino.jooq.generated.tables.records.LinksRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dockerino.jooq.generated.tables.Links.LINKS;

@Repository
public class ShortUrlRepository {

    private final DSLContext dsl;

    public ShortUrlRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public ShortUrl findByShortCode(String shortCode) {
        LinksRecord record = dsl.selectFrom(LINKS)
                .where(LINKS.SHORT_CODE.eq(shortCode))
                .fetchOne();

        return mapRecordToShortUrl(record);
    }

    public List<ShortUrl> findByUserId(UUID userId) {
        Result<LinksRecord> records = dsl.selectFrom(LINKS)
                .where(LINKS.USER_ID.eq(userId))
                .orderBy(LINKS.CREATED_AT.desc())
                .fetch();
        return records.stream().map(this::mapRecordToShortUrl).collect(Collectors.toList());
    }

    public boolean existsByShortCode(String shortCode) {
        return dsl.fetchExists(
                dsl.selectFrom(LINKS)
                        .where(LINKS.SHORT_CODE.eq(shortCode))
        );
    }

    public ShortUrl save(String shortCode, String originalUrl, UUID userId) {
        return dsl.insertInto(LINKS)
                .set(LINKS.SHORT_CODE, shortCode)
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
                r.getShortCode(),
                r.getOriginalUrl(),
                r.getUserId(),
                r.getCreatedAt());
    }
}
