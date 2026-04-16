package chdaeseung.accountbook.bank.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBankAccount is a Querydsl query type for BankAccount
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBankAccount extends EntityPathBase<BankAccount> {

    private static final long serialVersionUID = 1033363211L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBankAccount bankAccount = new QBankAccount("bankAccount");

    public final StringPath accountName = createString("accountName");

    public final NumberPath<Long> balance = createNumber("balance", Long.class);

    public final StringPath bankName = createString("bankName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath negativeBalanceAllowed = createBoolean("negativeBalanceAllowed");

    public final EnumPath<BankAccountType> type = createEnum("type", BankAccountType.class);

    public final chdaeseung.accountbook.user.entity.QUser user;

    public QBankAccount(String variable) {
        this(BankAccount.class, forVariable(variable), INITS);
    }

    public QBankAccount(Path<? extends BankAccount> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBankAccount(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBankAccount(PathMetadata metadata, PathInits inits) {
        this(BankAccount.class, metadata, inits);
    }

    public QBankAccount(Class<? extends BankAccount> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new chdaeseung.accountbook.user.entity.QUser(forProperty("user")) : null;
    }

}

