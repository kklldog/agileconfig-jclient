package mjzhou.agileconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomServers
{
    private String[] _serverUrls;
    private List<Integer> _serverIndexs;
    private int _startIndex = -1;
    public RandomServers(String[] serverUrls)
    {
        _serverIndexs = new ArrayList<>();
        if (serverUrls != null && serverUrls.length > 0)
        {
            _serverUrls = serverUrls;
        }
        else
        {
            throw new IllegalArgumentException("argument serverUrls can not be null or empty .");
        }

        int randomIndex = new Random().nextInt(_serverUrls.length);
        _serverIndexs.add(randomIndex);

        int index = randomIndex + 1;
        while (true)
        {
            if (index == randomIndex)
            {
                break;
            }
            if (index >= _serverUrls.length)
            {
                index = index - _serverUrls.length;
                continue;
            }
            _serverIndexs.add(index);
            index++;
        }
    }

    private int getNextIndex()
    {
        if (isComplete())
        {
            return  -1;
        }
        _startIndex++;

        return _startIndex;
    }

    public boolean isComplete()
    {
        return _startIndex + 1 >= _serverIndexs.stream().count();
    }

    public String next()
    {
        int idx = getNextIndex();
        if (idx == -1) {
            return  "";
        }
        return _serverUrls[_serverIndexs.get(idx)];
    }
}