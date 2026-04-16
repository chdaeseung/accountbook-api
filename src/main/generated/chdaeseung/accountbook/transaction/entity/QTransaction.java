package chdaeseung.accountbook.transaction.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTransaction is a Querydsl query type for Transaction
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTransaction extends EntityPathBase<Transaction> {

    private static final long serialVersionUID = 917870448L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTransaction transaction = new QTransaction("transaction");

    public final NumberPath<Long> amount = createNumber("amount", Long.class);

    public final chdaeseung.accountbook.bank.entity.QBankAccount bankAccount;

    public final chdaeseung.accountbook.category.entity.QCategory category;

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final EnumPath<ExpenseType> expenseType = createEnum("expenseType", ExpenseType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final chdaeseung.accountbook.recurring.entity.QRecurringTransaction recurringTransaction;

    public final BooleanPath transfer = createBoolean("transfer");

    public final StringPath transferGroupKey = createString("transferGroupKey");

    public final EnumPath<TransactionType> type = createEnum("type", TransactionType.class);

    public final chdaeseung.accountbook.user.entity.QUser user;

    public QTransaction(String variable) {
        this(Transaction.class, forVariable(variable), INITS);
    }

    public QTransaction(Path<? extends Transaction> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTransaction(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTransaction(PathMetadata metadata, PathInits inits) {
        this(Transaction.class, metadata, inits);
    }

    public QTransaction(Class<? extends Transaction> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bankAccount = inits.isInitialized("bankAccount") ? new chdaeseung.accountbook.bank.entity.QBankAccount(forProperty("bankAccount"), inits.get("bankAccount")) : null;
        this.category = inits.isInitialized("category") ? new chdaeseung.accountbook.category.entity.QCategory(forProperty("category"), inits.get("category")) : null;
        this.recurringTransaction = inits.isInitialized("recurringTransaction") ? new chdaeseung.accountbook.recurring.entity.QRecurringTransaction(forProperty("recurringTransaction"), inits.get("recurringTransaction")) : null;
        this.user = inits.isInitialized("user") ? new chdaeseung.accountbook.user.entity.QUser(forProperty("user")) : null;
    }

}

