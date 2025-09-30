package com.dockerino.demo.repository;

import com.dockerino.demo.exception.ShortUrlNotFoundException;
import com.dockerino.demo.model.ShortUrl;
import com.dockerino.jooq.generated.tables.records.LinksRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dockerino.jooq.generated.tables.Links.LINKS;

@Repository
public class ShortUrlRepository {

    private final DSLContext dsl;

    public ShortUrlRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    private ShortUrl mapRecordToShortUrl(Record r) {
        if (r == null) return null;
        return new ShortUrl(
                r.get(LINKS.ID, Long.class),
                r.get(LINKS.SHORT_CODE, String.class),
                r.get(LINKS.ORIGINAL_URL, String.class),
                r.get(LINKS.USER_ID, UUID.class),
                r.get(LINKS.CREATED_AT, LocalDateTime.class)
        );
    }

    Optional<ShortUrl> findById(Long id) {
        Record record = dsl.selectFrom(LINKS)
                .where(LINKS.ID.eq(id))
                .fetchOne();
        return Optional.ofNullable(mapRecordToShortUrl(record));
    }

    public Optional<ShortUrl> findByShortCode(String shortCode) {
        Record record = dsl.selectFrom(LINKS)
                .where(LINKS.SHORT_CODE.eq(shortCode))
                .fetchOne();
        return Optional.ofNullable(mapRecordToShortUrl(record));
    }

    public List<ShortUrl> findByUserId(UUID userId) {
        Result<LinksRecord> records = dsl.selectFrom(LINKS)
                .where(LINKS.USER_ID.eq(userId))
                .orderBy(LINKS.CREATED_AT.desc())
                .fetch();
        return records.stream().map(this::mapRecordToShortUrl).collect(Collectors.toList());
    }

    public boolean existsByShortCode(String shortCode) {
        return dsl.fetchExists(dsl.selectFrom(LINKS).where(LINKS.SHORT_CODE.eq(shortCode)));
    }

    @Transactional
    public ShortUrl save(ShortUrl shortUrl) {
        if (shortUrl.getId() == null) {
            return dsl.insertInto(LINKS)
                    .set(LINKS.SHORT_CODE, shortUrl.getShortCode())
                    .set(LINKS.ORIGINAL_URL, shortUrl.getOriginalUrl())
                    .set(LINKS.USER_ID, shortUrl.getUserId())
                    .set(LINKS.CREATED_AT, OffsetDateTime.now())
                    .returning()
                    .fetchOne(this::mapRecordToShortUrl);
        } else {
            dsl.update(LINKS)
                    .set(LINKS.SHORT_CODE, shortUrl.getShortCode())
                    .set(LINKS.ORIGINAL_URL, shortUrl.getOriginalUrl())
                    .set(LINKS.USER_ID, shortUrl.getUserId())
                    .where(LINKS.ID.eq(shortUrl.getId()))
                    .execute();
            return findById(shortUrl.getId()).orElseThrow(ShortUrlNotFoundException::new);
        }
    }
}
