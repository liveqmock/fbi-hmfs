package dep.hmfs.online.cmb.domain.base;

import java.io.Serializable;

public abstract class TIA implements Serializable {
    public abstract TIAHeader getHeader();
    public abstract TIABody getBody();
}
