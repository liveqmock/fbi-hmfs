package dep.hmfs.online.processor.cmb.domain.base;

import java.io.Serializable;

public abstract class TOA implements Serializable {
    public abstract TOAHeader getHeader();
    public abstract TOABody getBody();
}
