%--------------------------------------------------------------------------
% File     : PLA001-1 : TPTP v7.0.0. Released v1.0.0.
% Domain   : Planning
% Problem  : Cheyenne to DesMoines, buying a loaf of bread on the way
% Version  : Especial.
% English  : The problem is to drive from Cheyenne, Wyoming to Des Moines,
%            Iowa and end up there with a loaf of bread. A portion of the
%            road map is expressed in clause form. The allowable actions
%            are to drive from a city to a neighboring city, to buy a loaf
%            of bread at a city, and to wait_at at a city for one unit
%            of time. Buying a loaf of bread takes one unit of time and
%            driving to a neighboring city takes two units of time.

% Refs     : [Pla81] Plaisted (1981), Theorem Proving with Abstraction
% Source   : [Pla81]
% Names    : - [Pla81]

% Status   : Unsatisfiable
% Rating   : 0.00 v6.1.0, 0.21 v6.0.0, 0.33 v5.5.0, 0.25 v5.4.0, 0.28 v5.3.0, 0.35 v5.2.0, 0.23 v5.1.0, 0.19 v5.0.0, 0.13 v4.1.0, 0.20 v4.0.1, 0.14 v3.4.0, 0.00 v3.2.0, 0.33 v3.1.0, 0.00 v2.7.0, 0.12 v2.6.0, 0.14 v2.4.0, 0.14 v2.3.0, 0.14 v2.2.1, 0.22 v2.1.0, 0.00 v2.0.0
% Syntax   : Number of clauses     :   16 (   0 non-Horn;  12 unit;  16 RR)
%            Number of atoms       :   22 (   0 equality)
%            Maximal clause size   :    3 (   1 average)
%            Number of predicates  :    2 (   0 propositional; 2-4 arity)
%            Number of functors    :   18 (  14 constant; 0-2 arity)
%            Number of variables   :   20 (   2 singleton)
%            Maximal term depth    :    3 (   1 average)
% SPC      : CNF_UNS_RFO_NEQ_HRN

% Comments :
%--------------------------------------------------------------------------
cnf(drive1,axiom,
    ( ~ at(FromCity,Loaves,Time,Situation)
    | ~ next_to(FromCity,ToCity)
    | at(ToCity,Loaves,s(s(Time)),drive(ToCity,Situation)) )).

cnf(drive2,axiom,
    ( ~ at(FromCity,Loaves,Time,Situation)
    | ~ next_to(ToCity,FromCity)
    | at(ToCity,Loaves,s(s(Time)),drive(ToCity,Situation)) )).

cnf(wait_in_city,axiom,
    ( ~ at(City,Loaves,Time,Situation)
    | at(City,Loaves,s(Time),wait_at(Situation)) )).

cnf(buy_in_city,axiom,
    ( ~ at(City,Loaves,Time,Situation)
    | at(City,s(Loaves),s(Time),buy(Situation)) )).

cnf(map1,hypothesis,
    ( next_to(winnemucca,elko) )).

cnf(map2,hypothesis,
    ( next_to(elko,saltLakeCity) )).

cnf(map3,hypothesis,
    ( next_to(saltLakeCity,rockSprings) )).

cnf(map4,hypothesis,
    ( next_to(rockSprings,laramie) )).

cnf(map5,hypothesis,
    ( next_to(laramie,cheyenne) )).

cnf(map6,hypothesis,
    ( next_to(cheyenne,northPlatte) )).

cnf(map7,hypothesis,
    ( next_to(northPlatte,grandIsland) )).

cnf(map8,hypothesis,
    ( next_to(grandIsland,lincoln) )).

cnf(map9,hypothesis,
    ( next_to(lincoln,omaha) )).

cnf(map10,hypothesis,
    ( next_to(omaha,desMoines) )).

cnf(initial,hypothesis,
    ( at(cheyenne,none,start,initial_situation) )).

cnf(prove_you_gat_get_there_with_bread,negated_conjecture,
    ( ~ at(desMoines,s(none),Time,Situation) )).

%--------------------------------------------------------------------------
