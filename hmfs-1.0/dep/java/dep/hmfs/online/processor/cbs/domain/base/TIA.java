package dep.hmfs.online.processor.cbs.domain.base;

import java.io.Serializable;

public abstract class TIA implements Serializable {
    public abstract TIAHeader getHeader();
    public abstract TIABody getBody();
}
