package org.gymify.services;

import java.util.List;

public interface IService <T>{
    public void Add(T t);
    public void Update(T t);
    public void Delete(T t);
    public List<T> Display();
}
