package chdaeseung.accountbook.transfer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTransfer is a Querydsl query type for Transfer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransfer extends EntityPathBase<Transfer> {

    private static final long serialVersionUID = -1877952352L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTransfer transfer = new QTransfer("transfer");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final chdaeseung.accountbook.bank.entity.QBankAccount fromAccount;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final chdaeseung.accountbook.bank.entity.QBankAccount toAccount;

    public final chdaeseung.accountbook.user.entity.QUser user;

    public QTransfer(String variable) {
        this(Transfer.class, forVariable(variable), INITS);
    }

    public QTransfer(Path<? extends Transfer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTransfer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTransfer(PathMetadata metadata, PathInits inits) {
        this(Transfer.class, metadata, inits);
    }

    public QTransfer(Class<? extends Transfer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fromAccount = inits.isInitialized("fromAccount") ? new chdaeseung.accountbook.bank.entity.QBankAccount(forProperty("fromAccount"), inits.get("fromAccount")) : null;
        this.toAccount = inits.isInitialized("toAccount") ? new chdaeseung.accountbook.bank.entity.QBankAccount(forProperty("toAccount"), inits.get("toAccount")) : null;
        this.user = inits.isInitialized("user") ? new chdaeseung.accountbook.user.entity.QUser(forProperty("user")) : null;
    }

}

