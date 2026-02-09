use tokio::{
    sync::AcquireError,
    time::error::Elapsed,
};

#[derive(Debug)]
#[allow(dead_code)]
pub enum CountdownLatchError {
    AcquireError(AcquireError),
    Elapsed(Elapsed),
}

impl From<AcquireError> for CountdownLatchError {
    fn from(value: AcquireError) -> Self {
        CountdownLatchError::AcquireError(value)
    }
}

impl From<Elapsed> for CountdownLatchError {
    fn from(value: Elapsed) -> Self {
        CountdownLatchError::Elapsed(value)
    }
}
