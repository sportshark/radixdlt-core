package org.radix.integration;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.radixdlt.common.EUID;
import com.radixdlt.consensus.tempo.Application;
import com.radixdlt.consensus.tempo.Tempo;
import com.radixdlt.delivery.LazyRequestDeliverer;
import com.radixdlt.store.LedgerEntryStore;
import com.radixdlt.store.berkeley.BerkeleyStoreModule;
import org.junit.After;
import org.junit.Before;
import org.radix.database.DatabaseEnvironment;
import org.radix.modules.Modules;
import org.radix.network2.messaging.MessageCentral;
import org.radix.network2.messaging.MessageCentralFactory;
import org.radix.properties.RuntimeProperties;
import org.radix.universe.system.LocalSystem;

import java.io.IOException;

import static org.mockito.Mockito.mock;

public class RadixTestWithStores extends RadixTest
{
	private Injector injector;
	private DatabaseEnvironment dbEnv;
	private LedgerEntryStore store;
	private Tempo tempo;

	@Before
	public void beforeEachRadixTest() {
		this.dbEnv = new DatabaseEnvironment();
		this.dbEnv.start();

		RuntimeProperties properties = Modules.get(RuntimeProperties.class);
		MessageCentral messageCentral = new MessageCentralFactory().createDefault(properties);
		Modules.put(MessageCentral.class, messageCentral);

		EUID self = LocalSystem.getInstance().getNID();
		injector = Guice.createInjector(
				new AbstractModule() {
					@Override
					protected void configure() {
						bind(EUID.class).annotatedWith(Names.named("self")).toInstance(self);
					}
				},
				new BerkeleyStoreModule(dbEnv)
		);

		store = injector.getInstance(LedgerEntryStore.class);
		tempo = new Tempo(
			mock(Application.class),
			ImmutableSet.of(),
			mock(LazyRequestDeliverer.class));
	}

	@After
	public void afterEachRadixTest() throws IOException {
		tempo.close();

		this.dbEnv.stop();
		Modules.remove(DatabaseEnvironment.class);

		MessageCentral messageCentral = Modules.get(MessageCentral.class);
		messageCentral.close();
		Modules.remove(MessageCentral.class);
	}

	protected DatabaseEnvironment getDbEnv() {
		return dbEnv;
	}

	protected LedgerEntryStore getStore() {
		return store;
	}

	protected Tempo getTempo() {
		return tempo;
	}
}
