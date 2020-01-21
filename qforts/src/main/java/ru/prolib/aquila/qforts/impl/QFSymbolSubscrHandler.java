package ru.prolib.aquila.qforts.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.builder.EqualsBuilder;

import ru.prolib.aquila.core.BusinessEntities.EditableSecurity;
import ru.prolib.aquila.core.BusinessEntities.MDLevel;
import ru.prolib.aquila.core.BusinessEntities.SubscrHandler;

public class QFSymbolSubscrHandler implements SubscrHandler {
	private final QFReactor reactor;
	private final EditableSecurity security;
	private final MDLevel level;
	private final AtomicBoolean closed;
	private final boolean confirm;
	private final CompletableFuture<Boolean> f_confirm;
	
	public QFSymbolSubscrHandler(QFReactor reactor, EditableSecurity security, MDLevel level, boolean confirm) {
		this.reactor = reactor;
		this.security = security;
		this.level = level;
		this.closed = new AtomicBoolean(false);
		this.confirm = confirm;
		f_confirm = new CompletableFuture<>();
		f_confirm.complete(confirm);
	}
	
	public QFSymbolSubscrHandler(QFReactor reactor, EditableSecurity security, MDLevel level) {
		this(reactor, security, level, true);
	}
	
	public QFReactor getReactor() {
		return reactor;
	}
	
	public EditableSecurity getSecurity() {
		return security;
	}
	
	public MDLevel getLevel() {
		return level;
	}
	
	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public void close() {
		if ( closed.compareAndSet(false, true) ) {
			reactor.unsubscribe(security, level);
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != QFSymbolSubscrHandler.class ) {
			return false;
		}
		QFSymbolSubscrHandler o = (QFSymbolSubscrHandler) other;
		return new EqualsBuilder()
				.append(o.reactor, reactor)
				.append(o.security, security)
				.append(o.level, level)
				.append(o.closed.get(), closed.get())
				.append(o.confirm, confirm)
				.build();
	}

	@Override
	public CompletableFuture<Boolean> getConfirmation() {
		return f_confirm;
	}

}
