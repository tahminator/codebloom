use std::sync::Arc;

use chrono::Duration;
use tokio::{
    sync::Semaphore,
    time::timeout,
};

use crate::utils::latch::error::CountdownLatchError;

/// [CountdownLatch] is the Rust equivalent to
/// [Java's CountdownLatch](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CountDownLatch.html),
/// which can be used to synchronize thread operations.
///
/// # Example
///
/// ```ignore
/// let latch = CountdownLatch::new(1);
///
/// tokio::spawn(async move {
///     if let Err(e) = client.start().await {
///         println!("Client error: {e:?}");
///     }
///     latch.count_down();
/// })
///
/// latch.wait().await;
/// ```
#[derive(Clone)]
pub struct CountdownLatch {
    count: u8,
    sem: Arc<Semaphore>,
}

#[allow(dead_code)]
pub trait Latch {
    fn new(n: u8) -> Self;

    fn count_down(&self) -> ();

    async fn wait(&self) -> Result<(), CountdownLatchError>;

    async fn wait_until(&self, ms: u32) -> Result<(), CountdownLatchError>;
}

impl Latch for CountdownLatch {
    /// Create a new [CountdownLatch] with the specified count of `n`.
    fn new(n: u8) -> Self {
        CountdownLatch {
            count: n,
            sem: Arc::new(Semaphore::new(0)),
        }
    }

    // Count down by a factor of `1`.
    fn count_down(&self) -> () {
        self.sem.add_permits(1);
    }

    /// Will wait until `count_down` has been called `n` times.
    async fn wait(&self) -> Result<(), CountdownLatchError> {
        let permit = self
            .sem
            .acquire_many(self.count as u32)
            .await
            .map_err(|e| CountdownLatchError::from(e))?;
        permit.forget();
        Ok(())
    }

    /// Will wait until `count_down` has been called `n` times or timeout if we have waited `ms`
    /// milliseconds.
    async fn wait_until(&self, ms: u32) -> Result<(), CountdownLatchError> {
        timeout(
            Duration::milliseconds(ms as i64).to_std().unwrap(),
            self.sem.acquire_many(self.count as u32),
        )
        .await
        .map_err(CountdownLatchError::from)?
        .map(|p| {
            p.forget();
        })
        .map_err(CountdownLatchError::from)
    }
}
